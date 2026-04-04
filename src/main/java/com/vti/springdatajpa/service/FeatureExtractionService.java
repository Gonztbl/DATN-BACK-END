package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.dto.UserAIAnalysisDTO;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureExtractionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletDailySnapshotRepository walletDailySnapshotRepository;
    private final TransactionRepository transactionRepository;
    private final TransferDetailRepository transferDetailRepository;

    private static final int LOOKBACK_DAYS = 90;
    private static final BigDecimal LOW_BALANCE_THRESHOLD = BigDecimal.valueOf(50000);

    /**
     * Extract 16 features for AI credit scoring model
     * 
     * @param user User entity
     * @param declaredIncome Declared income from form
     * @param jobSegmentNum Job segment from form
     * @return float array with 16 features in order
     */
    public float[] extractFeatures(User user, BigDecimal declaredIncome, String jobSegmentNum) {
        float[] features = new float[16];
        try {
            LocalDate fromDate = LocalDate.now().minusDays(LOOKBACK_DAYS);
            LocalDateTime fromDateTime = fromDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime toDateTime = LocalDateTime.now();

            Wallet wallet = user.getWallet();
            if (wallet == null) {
                log.warn("User {} has no wallet", user.getId());
                return features; // Return zero-filled array
            }

            Integer walletId = wallet.getId();

            // 1. job_segment_num (from form)
            features[0] = parseJobSegment(jobSegmentNum);

            // 2. declared_income (from form)
            features[1] = declaredIncome != null ? declaredIncome.floatValue() : 0f;

            // 3. age (calculated)
            features[2] = calculateAge(user.getDateOfBirth());

            // 4. account_age_days (calculated)
            features[3] = calculateAccountAgeDays(user.getCreatedAt());

            // 5. kyc_level (from users table)
            features[4] = user.getKycLevel() != null ? user.getKycLevel().floatValue() : 1f;

            // 6. avg_balance (from wallet_daily_snapshots)
            features[5] = getAverageBalance(walletId, fromDate);

            // 6. monthly_inflow_mean (total IN / 3 months) - Index 6 in Python
            features[6] = getMonthlyInflowMean(walletId, fromDateTime, toDateTime);

            // 7. monthly_outflow_mean (total OUT / 3 months) - Index 7 in Python
            features[7] = getMonthlyOutflowMean(walletId, fromDateTime, toDateTime);

            // 8. largest_inflow (max IN transaction) - Index 8 in Python
            features[8] = getLargestInflow(walletId, fromDateTime, toDateTime);

            // 9. balance_volatility (standard deviation) - Index 9 in Python
            features[9] = getBalanceVolatility(walletId, fromDate);

            // 10. low_balance_days_ratio (days with balance < 50k / total days) - Index 10 in Python
            features[10] = getLowBalanceDaysRatio(walletId, fromDate);

            // 11. tx_count (total transaction count) - Index 11 in Python
            features[11] = getTransactionCount(walletId, fromDateTime, toDateTime);

            // 12. unique_receivers (distinct receivers) - Index 12 in Python
            features[12] = getUniqueReceivers(walletId, fromDateTime);

            // 13. peer_transfer_ratio (peer transfers / total tx) - Index 13 in Python
            features[13] = getPeerTransferRatio(walletId, fromDateTime, toDateTime);

            // 14. rejected_tx_ratio (failed tx / total tx) - Index 14 in Python
            features[14] = getRejectedTransactionRatio(walletId, fromDateTime, toDateTime);

            // 15. spend_income_ratio (monthly_outflow / declared_income) - Index 15 in Python
            float monthlyOutflow = features[7]; // monthly_outflow_mean is at index 7 now
            float income = features[1];
            features[15] = income != 0 ? monthlyOutflow / income : 0f;

            log.info("Features extracted for user {}: {}", user.getId(), features);

        } catch (Exception e) {
            log.error("Error extracting features for user {}", user.getId(), e);
        }

        return features;
    }

    private float parseJobSegment(String jobSegment) {
        if (jobSegment == null) return 0f;
        
        String segment = jobSegment.toUpperCase();
        
        // Mapping based on training mock data patterns:
        // 1: OFFICE_WORKER, 2: FREELANCER, 3: MERCHANT, 0: Others/Unemployed
        if (segment.equals("OFFICE_WORKER")) {
            return 1.0f;
        } else if (segment.equals("FREELANCER")) {
            return 2.0f;
        } else if (segment.equals("MERCHANT")) {
            return 3.0f;
        } else if (segment.equals("STUDENT")) {
            return 0.0f;
        } else {
            // If it's already a number string, parse it
            try {
                return Float.parseFloat(jobSegment);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
    }

    private float calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0f;
        return (float) (LocalDate.now().getYear() - dateOfBirth.getYear());
    }

    private float calculateAccountAgeDays(LocalDateTime createdAt) {
        if (createdAt == null) return 0f;
        return (float) java.time.temporal.ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }

    private float getAverageBalance(Integer walletId, LocalDate fromDate) {
        try {
            Double avg = walletDailySnapshotRepository.getAverageBalance(walletId, fromDate);
            return avg != null ? avg.floatValue() : 0f;
        } catch (Exception e) {
            log.warn("Error calculating average balance for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getBalanceVolatility(Integer walletId, LocalDate fromDate) {
        try {
            Double stdDev = walletDailySnapshotRepository.getBalanceStdDev(walletId, fromDate);
            return stdDev != null ? stdDev.floatValue() : 0f;
        } catch (Exception e) {
            log.warn("Error calculating balance volatility for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getLowBalanceDaysRatio(Integer walletId, LocalDate fromDate) {
        try {
            Long lowBalanceDays = walletDailySnapshotRepository.countLowBalanceDays(walletId, fromDate);
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(fromDate, LocalDate.now());
            return totalDays > 0 ? (float) lowBalanceDays / totalDays : 0f;
        } catch (Exception e) {
            log.warn("Error calculating low balance ratio for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getMonthlyInflowMean(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Double totalInflow = transactionRepository.sumAmountByWalletIdAndDirectionBetween(
                    walletId, TransactionDirection.IN, fromDateTime, toDateTime
            );
            if (totalInflow == null || totalInflow == 0) return 0f;
            // Divide by 3 for 3-month average
            return (float) (totalInflow / 3.0);
        } catch (Exception e) {
            log.warn("Error calculating monthly inflow mean for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getMonthlyOutflowMean(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Double totalOutflow = transactionRepository.sumAmountByWalletIdAndDirectionBetween(
                    walletId, TransactionDirection.OUT, fromDateTime, toDateTime
            );
            if (totalOutflow == null || totalOutflow == 0) return 0f;
            // Divide by 3 for 3-month average
            return (float) (totalOutflow / 3.0);
        } catch (Exception e) {
            log.warn("Error calculating monthly outflow mean for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getLargestInflow(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Double maxInflow = transactionRepository.getMaxInflowAmount(walletId, fromDateTime, toDateTime);
            return maxInflow != null ? maxInflow.floatValue() : 0f;
        } catch (Exception e) {
            log.warn("Error calculating largest inflow for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getTransactionCount(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Long count = transactionRepository.countTransactionsInRange(walletId, fromDateTime, toDateTime);
            return count != null ? count.floatValue() : 0f;
        } catch (Exception e) {
            log.warn("Error calculating transaction count for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getRejectedTransactionRatio(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Long failedCount = transactionRepository.countFailedTransactions(walletId, fromDateTime, toDateTime);
            Long totalCount = transactionRepository.countTransactionsInRange(walletId, fromDateTime, toDateTime);
            
            if (totalCount == null || totalCount == 0) return 0f;
            return (float) failedCount / totalCount;
        } catch (Exception e) {
            log.warn("Error calculating rejected transaction ratio for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getPeerTransferRatio(Integer walletId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        try {
            Long peerTransfers = transferDetailRepository.countPeerTransfersInRange(walletId, fromDateTime);
            Long totalCount = transactionRepository.countTransactionsInRange(walletId, fromDateTime, toDateTime);
            
            if (totalCount == null || totalCount == 0) return 0f;
            return (float) peerTransfers / totalCount;
        } catch (Exception e) {
            log.warn("Error calculating peer transfer ratio for wallet {}", walletId, e);
            return 0f;
        }
    }

    private float getUniqueReceivers(Integer walletId, LocalDateTime fromDateTime) {
        try {
            Long uniqueReceivers = transferDetailRepository.countDistinctReceiversInRange(walletId, fromDateTime);
            return uniqueReceivers != null ? uniqueReceivers.floatValue() : 0f;
        } catch (Exception e) {
            log.warn("Error calculating unique receivers for wallet {}", walletId, e);
            return 0f;
        }
    }

    /**
     * Get actual average monthly income from wallet transaction history (last 90 days)
     * Used for fraud detection - compare against declared income
     */
    public BigDecimal getActualAverageMonthlyIncome(User user) {
        try {
            Wallet wallet = user.getWallet();
            if (wallet == null) {
                log.warn("User {} has no wallet", user.getId());
                return BigDecimal.ZERO;
            }

            LocalDate fromDate = LocalDate.now().minusDays(LOOKBACK_DAYS);
            LocalDateTime fromDateTime = fromDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime toDateTime = LocalDateTime.now();

            Double totalInflow = transactionRepository.sumAmountByWalletIdAndDirectionBetween(
                    wallet.getId(), TransactionDirection.IN, fromDateTime, toDateTime
            );
            
            if (totalInflow == null || totalInflow == 0) {
                return BigDecimal.ZERO;
            }
            
            // Average over 3 months
            BigDecimal monthlyAverage = BigDecimal.valueOf(totalInflow / 3.0);
            log.debug("User {} actual monthly income: {} from total inflow: {}", 
                    user.getId(), monthlyAverage, totalInflow);
            
            return monthlyAverage;
        } catch (Exception e) {
            log.error("Error calculating actual monthly income for user {}: {}", user.getId(), e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get complete AI analysis for a user - for admin dashboard
     * Returns all wallet and transaction analysis features
     */
    public UserAIAnalysisDTO getUserAIAnalysis(User user) {
        try {
            Wallet wallet = user.getWallet();
            if (wallet == null) {
                log.warn("User {} has no wallet", user.getId());
                wallet = new Wallet();
                wallet.setBalance(0.0);
                wallet.setAvailableBalance(0.0);
            }

            LocalDate fromDate = LocalDate.now().minusDays(LOOKBACK_DAYS);
            LocalDateTime fromDateTime = fromDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime toDateTime = LocalDateTime.now();

            Integer walletId = wallet.getId();

            // Calculate all features (converting float to Double)
            Double monthlyInflowMean = Double.valueOf(getMonthlyInflowMean(walletId, fromDateTime, toDateTime));
            Double monthlyOutflowMean = Double.valueOf(getMonthlyOutflowMean(walletId, fromDateTime, toDateTime));
            Double transactionCount = Double.valueOf(getTransactionCount(walletId, fromDateTime, toDateTime));
            Double accountAgeDays = Double.valueOf(calculateAccountAgeDays(user.getCreatedAt()));
            Double avgBalance = Double.valueOf(getAverageBalance(walletId, fromDate));
            Double balanceVolatility = Double.valueOf(getBalanceVolatility(walletId, fromDate));
            Double rejectedTxRatio = Double.valueOf(getRejectedTransactionRatio(walletId, fromDateTime, toDateTime) * 100); // Convert to %
            
            // Calculate spend/income ratio
            Double spendIncomeRatio = (monthlyInflowMean > 0) ? 
                    (monthlyOutflowMean / monthlyInflowMean * 100) : 0.0;

            // Build DTO
            UserAIAnalysisDTO analysis = new UserAIAnalysisDTO();
            analysis.setUserId(user.getId());
            analysis.setFullName(user.getFullName());
            analysis.setEmail(user.getEmail());
            analysis.setPhone(user.getPhone());
            analysis.setUserName(user.getUserName());
            
            analysis.setWalletBalance(wallet.getBalance() != null ? wallet.getBalance() : 0.0);
            analysis.setAvailableBalance(wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : 0.0);
            
            analysis.setMonthlyInflowMean(monthlyInflowMean);
            analysis.setMonthlyOutflowMean(monthlyOutflowMean);
            analysis.setTransactionCount(transactionCount);
            analysis.setAccountAgeDays(accountAgeDays);
            analysis.setSpendIncomeRatio(spendIncomeRatio);
            analysis.setBalanceVolatility(balanceVolatility);
            analysis.setRejectedTransactionRatio(rejectedTxRatio);
            
            // Additional features
            analysis.setAge(Double.valueOf(calculateAge(user.getDateOfBirth())));
            analysis.setAvgBalance(avgBalance);
            analysis.setLowBalanceDaysRatio(Double.valueOf(getLowBalanceDaysRatio(walletId, fromDate) * 100));
            analysis.setLargestInflow(Double.valueOf(getLargestInflow(walletId, fromDateTime, toDateTime)));
            analysis.setPeerTransferRatio(Double.valueOf(getPeerTransferRatio(walletId, fromDateTime, toDateTime) * 100));
            analysis.setUniqueReceivers(Double.valueOf(getUniqueReceivers(walletId, fromDateTime)));
            
            log.info("Generated AI analysis for user {}", user.getId());
            return analysis;
        } catch (Exception e) {
            log.error("Error generating AI analysis for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to generate AI analysis for user: " + user.getId());
        }
    }
}

