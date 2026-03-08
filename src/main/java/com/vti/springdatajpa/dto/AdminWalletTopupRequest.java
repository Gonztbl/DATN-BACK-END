package com.vti.springdatajpa.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class AdminWalletTopupRequest {
    @NotNull(message = "Wallet ID is required")
    private Integer walletId;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amountAdd;
}
