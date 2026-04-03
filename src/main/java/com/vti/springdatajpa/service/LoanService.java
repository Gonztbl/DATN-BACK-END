package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.LoanRequestApplyDTO;
import com.vti.springdatajpa.dto.LoanRequestResponseDTO;
import com.vti.springdatajpa.dto.LoanSummaryDTO;
import com.vti.springdatajpa.dto.AdminLoanDetailDTO;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.LoanStatus;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRequestRepository loanRequestRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final FeatureExtractionService featureExtractionService;
    private final CreditScoringEngine creditScoringEngine;

    /**
     * Customer applies for a loan
     * Steps:
     * 1. Create loan request record with PENDING_AI status
     * 2. Extract 16 features from wallet history
     * 3. Run AI scoring
     * 4. Update loan status based on AI score
     */
    @Transactional
    public LoanRequestResponseDTO applyForLoan(Integer userId, LoanRequestApplyDTO applyDTO) {
        log.info("Processing loan application for user: {}", userId);
        
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // Create loan request (initial status: PENDING_AI)
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUser(user);
        loanRequest.setAmount(applyDTO.getAmount());
        loanRequest.setTerm(applyDTO.getTerm());
        loanRequest.setPurpose(applyDTO.getPurpose());
        loanRequest.setDeclaredIncome(applyDTO.getDeclaredIncome());
        loanRequest.setJobSegmentNum(applyDTO.getJobSegmentNum());
        loanRequest.setFinalStatus(LoanStatus.PENDING_AI);

        // Save initial record
        loanRequest = loanRequestRepository.save(loanRequest);
        log.info("Loan request created with ID: {}", loanRequest.getId());

        // ⚠️ FRAUD DETECTION: Verify declared income vs actual wallet history
        FraudCheckResult fraudCheck = verifyDeclaredIncome(user, applyDTO.getDeclaredIncome());
        if (fraudCheck.isSuspicious()) {
            log.warn("⚠️ FRAUD DETECTED - Loan {}: declared={}, actual={}, inconsistency={}%", 
                    loanRequest.getId(), 
                    applyDTO.getDeclaredIncome(), 
                    fraudCheck.getActualMonthlyIncome(),
                    fraudCheck.getInconsistencyRatio() * 100);
            
            loanRequest.setAdminNote(
                String.format("⚠️ FRAUD ALERT: Declared income VND %,d but actual average monthly inflow only VND %,d (%.0f%% mismatch). Requires manual verification.",
                    applyDTO.getDeclaredIncome().longValue(),
                    fraudCheck.getActualMonthlyIncome().longValue(),
                    fraudCheck.getInconsistencyRatio() * 100)
            );
        }

        // Extract features from wallet history
        float[] features = featureExtractionService.extractFeatures(
                user,
                applyDTO.getDeclaredIncome(),
                applyDTO.getJobSegmentNum()
        );
        log.info("Features extracted for loan {}: {}", loanRequest.getId(), features);

        // Run AI scoring
        double aiScore = creditScoringEngine.predict(features);
        String aiDecision = creditScoringEngine.getDecision(aiScore);
        log.info("AI scoring completed for loan {}: score={}, decision={}", 
                loanRequest.getId(), aiScore, aiDecision);

        // Update loan request with AI results
        loanRequest.setAiScore(aiScore);
        loanRequest.setAiDecision(aiDecision);

        // Set status based on AI decision
        if ("REJECTED_BY_AI".equals(aiDecision)) {
            loanRequest.setFinalStatus(LoanStatus.REJECTED);
            if (loanRequest.getAdminNote() == null) {
                loanRequest.setAdminNote("Rejected by AI scoring: high default risk");
            }
        } else {
            // PASSED_AI -> Pending admin review
            // If fraud detected, keep flag so admin can investigate
            loanRequest.setFinalStatus(LoanStatus.PENDING_ADMIN);
        }

        loanRequest = loanRequestRepository.save(loanRequest);
        log.info("Loan request {} updated with AI decision: status={}", 
                loanRequest.getId(), loanRequest.getFinalStatus());

        return convertToResponseDTO(loanRequest);
    }

    /**
     * Verify declared income against actual wallet transaction history
     * Detects income inflation/fraud
     */
    private FraudCheckResult verifyDeclaredIncome(User user, BigDecimal declaredIncome) {
        try {
            // Get user's actual average monthly inflow from transactions
            BigDecimal actualMonthlyIncome = featureExtractionService.getActualAverageMonthlyIncome(user);
            
            // If declared income is 0 or null, any actual income is ok
            if (declaredIncome == null || declaredIncome.signum() <= 0) {
                return new FraudCheckResult(false, actualMonthlyIncome, 0.0);
            }
            
            // Check inconsistency ratio
            // Threshold: If declared > 150% of actual, flag as suspicious
            double inconsistencyRatio = 1.0 - (actualMonthlyIncome.doubleValue() / declaredIncome.doubleValue());
            double FRAUD_THRESHOLD = 0.5; // Flag if declared is >150% of actual
            
            boolean isSuspicious = inconsistencyRatio > FRAUD_THRESHOLD && actualMonthlyIncome.doubleValue() > 0;
            
            log.debug("Income verification: declared={}, actual={}, ratio={:.2f}", 
                    declaredIncome, actualMonthlyIncome, inconsistencyRatio);
            
            return new FraudCheckResult(isSuspicious, actualMonthlyIncome, inconsistencyRatio);
        } catch (Exception e) {
            log.error("Error during income verification: {}", e.getMessage());
            return new FraudCheckResult(false, BigDecimal.ZERO, 0.0);
        }
    }

    /**
     * Helper class for fraud detection result
     */
    public static class FraudCheckResult {
        private final boolean suspicious;
        private final BigDecimal actualMonthlyIncome;
        private final double inconsistencyRatio;

        public FraudCheckResult(boolean suspicious, BigDecimal actualMonthlyIncome, double inconsistencyRatio) {
            this.suspicious = suspicious;
            this.actualMonthlyIncome = actualMonthlyIncome;
            this.inconsistencyRatio = inconsistencyRatio;
        }

        public boolean isSuspicious() { return suspicious; }
        public BigDecimal getActualMonthlyIncome() { return actualMonthlyIncome; }
        public double getInconsistencyRatio() { return inconsistencyRatio; }
    }

    /**
     * Get loan details for a specific loan
     */
    public LoanRequestResponseDTO getLoanById(Long loanId, Integer userId) {
        LoanRequest loanRequest = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found: " + loanId));

        // Verify ownership (customer can only see their own loans)
        if (!loanRequest.getUser().getId().equals(userId)) {
            throw new IllegalAccessError("Access denied to loan: " + loanId);
        }

        return convertToResponseDTO(loanRequest);
    }

    /**
     * Get all loans for a customer
     */
    public Page<LoanRequestResponseDTO> getMyLoans(Integer userId, Pageable pageable) {
        Page<LoanRequest> loans = loanRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return loans.map(this::convertToResponseDTO);
    }

    /**
     * Summarize loans for dashboard (user-specific)
     */
    public LoanSummaryDTO getLoanSummary(Integer userId) {
        List<LoanRequest> loans = loanRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        int totalLoans = loans.size();
        BigDecimal totalLoanAmount = loans.stream()
                .map(LoanRequest::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long approved = loanRequestRepository.countByUser_IdAndFinalStatus(userId, LoanStatus.APPROVED);
        long pendingAdmin = loanRequestRepository.countByUser_IdAndFinalStatus(userId, LoanStatus.PENDING_ADMIN);
        long rejected = loanRequestRepository.countByUser_IdAndFinalStatus(userId, LoanStatus.REJECTED);

        double averageAiScore = loans.stream()
                .map(LoanRequest::getAiScore)
                .filter(score -> score != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        String creditRating;
        if (totalLoans == 0) {
            creditRating = "No data";
        } else if (averageAiScore <= 0.3) {
            creditRating = "Excellent";
        } else if (averageAiScore <= 0.5) {
            creditRating = "Good";
        } else if (averageAiScore <= 0.7) {
            creditRating = "Fair";
        } else {
            creditRating = "Poor";
        }

        LoanSummaryDTO summary = new LoanSummaryDTO();
        summary.setTotalLoans(totalLoans);
        summary.setTotalLoanAmount(totalLoanAmount);
        summary.setApprovedLoans((int) approved);
        summary.setPendingAdminLoans((int) pendingAdmin);
        summary.setRejectedLoans((int) rejected);
        summary.setAverageAiScore(Math.round(averageAiScore * 100.0) / 100.0);
        summary.setCreditRating(creditRating);
        summary.setStatusDisplay("Loan dashboard summary");
        summary.setDescriptionText("This summary is based on all your loan requests and AI scoring.");

        return summary;
    }

    /**
     * Admin: Get list of loans pending admin review
     */
    public Page<LoanRequest> getPendingAdminReviewLoans(Pageable pageable) {
        return loanRequestRepository.findByFinalStatusOrderByCreatedAtDesc(LoanStatus.PENDING_ADMIN, pageable);
    }

    /**
     * Admin: Get loan with detailed user and AI information
     */
    public AdminLoanDetailDTO getAdminLoanDetail(Long loanId) {
        LoanRequest loanRequest = loanRequestRepository.findByIdWithDetails(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found: " + loanId));

        return convertToAdminDetailDTO(loanRequest);
    }

    /**
     * Admin: Approve loan and disburse funds
     * Steps:
     * 1. Update loan status to APPROVED
     * 2. Add loan amount to user's wallet (balance and availableBalance)
     * 3. Create transaction record (LOAN_DISBURSEMENT, IN)
     * 4. Use @Transactional for atomicity
     */
    @Transactional
    public LoanRequestResponseDTO approveLoan(Long loanId, String adminNote) {
        Integer adminId = getAuthenticatedUserId();
        log.info("Admin {} approving loan {}", adminId, loanId);

        // Get loan
        LoanRequest loanRequest = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found: " + loanId));

        // Verify pending admin review
        if (loanRequest.getFinalStatus() != LoanStatus.PENDING_ADMIN) {
            throw new IllegalStateException("Loan is not pending admin review: " + loanRequest.getFinalStatus());
        }

        // Update loan status
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found: " + adminId));
        loanRequest.setAdmin(admin);
        loanRequest.setFinalStatus(LoanStatus.APPROVED);
        loanRequest.setAdminNote(adminNote != null ? adminNote : "Approved by admin");
        loanRequest.setUpdatedAt(LocalDateTime.now());
        loanRequest = loanRequestRepository.save(loanRequest);

        // Get wallet
        Wallet wallet = loanRequest.getUser().getWallet();
        if (wallet == null) {
            throw new IllegalStateException("User wallet not found for loan: " + loanId);
        }

        // Add loan amount to wallet balance
        BigDecimal loanAmount = loanRequest.getAmount();
        Double newBalance = (wallet.getBalance() != null ? wallet.getBalance() : 0.0) + loanAmount.doubleValue();
        Double newAvailableBalance = (wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : 0.0) + loanAmount.doubleValue();
        wallet.setBalance(newBalance);
        wallet.setAvailableBalance(newAvailableBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        log.info("Wallet {} updated: +{} VND", wallet.getId(), loanAmount);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDirection(TransactionDirection.IN);
        transaction.setAmount(loanAmount.doubleValue());
        transaction.setFee(0.0);
        transaction.setBalanceBefore((wallet.getBalance() != null ? wallet.getBalance() : 0.0) - loanAmount.doubleValue());
        transaction.setBalanceAfter(newBalance);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReferenceId("LOAN_" + loanId);
        transaction.setMetadata("{\"loanId\":" + loanId + ",\"type\":\"LOAN_DISBURSEMENT\"}");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        log.info("Transaction created for loan disbursement: {}", transaction.getId());

        log.info("Loan {} approved successfully", loanId);
        return convertToResponseDTO(loanRequest);
    }

    /**
     * Admin: Reject loan
     */
    @Transactional
    public LoanRequestResponseDTO rejectLoan(Long loanId, String adminNote) {
        Integer adminId = getAuthenticatedUserId();
        log.info("Admin {} rejecting loan {}", adminId, loanId);

        LoanRequest loanRequest = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found: " + loanId));

        // Verify pending admin review
        if (loanRequest.getFinalStatus() != LoanStatus.PENDING_ADMIN) {
            throw new IllegalStateException("Loan is not pending admin review: " + loanRequest.getFinalStatus());
        }

        // Update loan status
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found: " + adminId));
        loanRequest.setAdmin(admin);
        loanRequest.setFinalStatus(LoanStatus.REJECTED);
        loanRequest.setAdminNote(adminNote != null ? adminNote : "Rejected by admin");
        loanRequest.setUpdatedAt(LocalDateTime.now());
        loanRequest = loanRequestRepository.save(loanRequest);

        log.info("Loan {} rejected successfully", loanId);
        return convertToResponseDTO(loanRequest);
    }

    /**
     * Convert LoanRequest entity to ResponseDTO
     */
    private LoanRequestResponseDTO convertToResponseDTO(LoanRequest loanRequest) {
        LoanRequestResponseDTO dto = new LoanRequestResponseDTO();
        dto.setId(loanRequest.getId());
        dto.setAmount(loanRequest.getAmount());
        dto.setTerm(loanRequest.getTerm());
        dto.setPurpose(loanRequest.getPurpose());
        dto.setDeclaredIncome(loanRequest.getDeclaredIncome());
        dto.setJobSegmentNum(loanRequest.getJobSegmentNum());
        dto.setAiScore(loanRequest.getAiScore());
        dto.setAiDecision(loanRequest.getAiDecision());
        dto.setAdminNote(loanRequest.getAdminNote());
        dto.setFinalStatus(loanRequest.getFinalStatus());
        dto.setCreatedAt(loanRequest.getCreatedAt());
        dto.setUpdatedAt(loanRequest.getUpdatedAt());
        
        // Add display text
        dto.setStatusDisplay(getStatusDisplay(loanRequest.getFinalStatus()));
        dto.setDescriptionText(getStatusDescription(loanRequest.getFinalStatus(), loanRequest.getAiScore()));
        
        return dto;
    }

    /**
     * Convert LoanRequest to AdminLoanDetailDTO
     */
    private AdminLoanDetailDTO convertToAdminDetailDTO(LoanRequest loanRequest) {
        AdminLoanDetailDTO dto = new AdminLoanDetailDTO();
        
        // Loan info
        dto.setLoanId(loanRequest.getId());
        dto.setAmount(loanRequest.getAmount());
        dto.setTerm(loanRequest.getTerm());
        dto.setPurpose(loanRequest.getPurpose());
        dto.setDeclaredIncome(loanRequest.getDeclaredIncome());
        dto.setJobSegmentNum(loanRequest.getJobSegmentNum());
        dto.setAiScore(loanRequest.getAiScore());
        dto.setAiDecision(loanRequest.getAiDecision());
        dto.setFinalStatus(loanRequest.getFinalStatus());
        dto.setAdminNote(loanRequest.getAdminNote());
        dto.setLoanCreatedAt(loanRequest.getCreatedAt());

        // User info
        User user = loanRequest.getUser();
        dto.setUserId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());
        dto.setKycLevel(user.getKycLevel());
        dto.setJobSegment(user.getJobSegment());

        // Wallet info
        Wallet wallet = user.getWallet();
        if (wallet != null) {
            dto.setWalletId(wallet.getId());
            dto.setWalletBalance(wallet.getBalance());
            dto.setAvailableBalance(wallet.getAvailableBalance());
        }

        return dto;
    }

    private String getStatusDisplay(LoanStatus status) {
        return switch (status) {
            case PENDING_AI -> "Đang chờ AI chấm điểm";
            case PENDING_ADMIN -> "Đang chờ admin xét duyệt";
            case APPROVED -> "Đã duyệt";
            case REJECTED -> "Đã từ chối";
        };
    }

    private String getStatusDescription(LoanStatus status, Double aiScore) {
        return switch (status) {
            case PENDING_AI -> "Hồ sơ đang được hệ thống AI phân tích";
            case PENDING_ADMIN -> "AI đã hoàn thành phân tích, chờ quản lý xem xét";
            case APPROVED -> "Hồ sơ đã được duyệt, tiền sẽ được cộng vào tài khoản";
            case REJECTED -> "Hồ sơ không được duyệt";
        };
    }

    public Integer getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        String identity = null;
        if (principal instanceof String) {
            identity = (String) principal;
        }
        if (identity == null) {
            throw new RuntimeException("Unsupported principal type for authenticated user");
        }

        final String finalIdentity = identity;
        return userRepository.findByUserName(finalIdentity)
                .or(() -> userRepository.findByEmail(finalIdentity))
                .map(User::getId)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found: " + finalIdentity));
    }
}
