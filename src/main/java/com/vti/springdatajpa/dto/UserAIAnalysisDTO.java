package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAIAnalysisDTO {
    // User Basic Info
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String userName;
    
    // Wallet Info
    private Double walletBalance;      // Số dư TB
    private Double availableBalance;

    // AI Analysis Features
    private Double monthlyInflowMean;      // Dòng tiền vào (VND)
    private Double monthlyOutflowMean;     // Dòng tiền ra (VND)
    private Double transactionCount;       // Số giao dịch
    private Double accountAgeDays;         // Tuổi tài khoản (ngày)
    private Double spendIncomeRatio;       // Tỷ lệ chi/thu (%)
    private Double balanceVolatility;      // Độ biến động số dư (VND)
    private Double rejectedTransactionRatio; // Giao dịch bị từ chối (%)
    
    // Additional AI Features
    private Double age;
    private Double avgBalance;
    private Double lowBalanceDaysRatio;
    private Double largestInflow;
    private Double largestOutflow;
    private Double peerTransferRatio;
    private Double uniqueReceivers;
}
