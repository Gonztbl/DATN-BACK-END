package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminWalletTopupResponse {
    private Integer transactionId;
    private Integer walletId;
    private Integer userId;
    private String accountNumber;
    private Double amountAdded;
    private Double previousBalance;
    private Double newBalance;
    private String status;
    private String message;
    private LocalDateTime timestamp;
}
