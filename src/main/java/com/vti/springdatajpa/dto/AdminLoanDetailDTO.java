package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoanDetailDTO {

    // Loan Request Info
    private Long loanId;
    private BigDecimal amount;
    private Integer term;
    private String purpose;
    private BigDecimal declaredIncome;
    private String jobSegmentNum;
    private Double aiScore;
    private String aiDecision;
    private LoanStatus finalStatus;
    private String adminNote;
    private LocalDateTime loanCreatedAt;

    // User Info
    private Integer userId;
    private String userName;
    private String email;
    private String phone;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private Integer kycLevel;
    private String jobSegment;

    // Wallet Info
    private Integer walletId;
    private Double walletBalance;
    private Double availableBalance;

    // AI Features (for admin review)
    private Float age;
    private Long accountAgeDays;
    private Float avgBalance;
    private Float balanceVolatility;
    private Float lowBalanceDaysRatio;
    private Float monthlyInflowMean;
    private Float monthlyOutflowMean;
    private Float largestInflow;
    private Long transactionCount;
    private Float rejectedTransactionRatio;
    private Float peerTransferRatio;
    private Long uniqueReceivers;

    // Computed
    private Float spendIncomeRatio;
}
