package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
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

            // 7. balance_volatility (standard deviation)
            features[6] = getBalanceVolatility(walletId, fromDate);

            // 8. low_balance_days_ratio (days with balance < 50k / total days)
            features[7] = getLowBalanceDaysRatio(walletId, fromDate);

            // 9. monthly_inflow_mean (total IN / 3 months)
            features[8] = getMonthlyInflowMean(walletId, fromDateTime, toDateTime);

            // 10. monthly_outflow_mean (total OUT / 3 months)
            features[9] = getMonthlyOutflowMean(walletId, fromDateTime, toDateTime);

            // 11. largest_inflow (max IN transaction)
            features[10] = getLargestInflow(walletId, fromDateTime, toDateTime);

            // 12. tx_count (total transaction count)
            features[11] = getTransactionCount(walletId, fromDateTime, toDateTime);

            // 13. rejected_tx_ratio (failed tx / total tx)
            features[12] = getRejectedTransactionRatio(walletId, fromDateTime, toDateTime);

            // 14. peer_transfer_ratio (peer transfers / total tx)
            features[13] = getPeerTransferRatio(walletId, fromDateTime, toDateTime);

            // 15. unique_receivers (distinct receivers)
            features[14] = getUniqueReceivers(walletId, fromDateTime);

            // 16. spend_income_ratio (monthly_outflow / declared_income)
            float monthlyOutflow = features[9];
            float income = features[1];
            features[15] = income != 0 ? monthlyOutflow / income : 0f;

            log.info("Features extracted for user {}: {}", user.getId(), features);

        } catch (Exception e) {
            log.error("Error extracting features for user {}", user.getId(), e);
        }

        return features;
    }

    private float parseJobSegment(String jobSegmentNum) {
        try {
            return jobSegmentNum != null ? Float.parseFloat(jobSegmentNum) : 0f;
        } catch (NumberFormatException e) {
            return 0f;
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
}
