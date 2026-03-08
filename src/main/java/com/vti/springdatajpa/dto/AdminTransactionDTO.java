package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminTransactionDTO {
    private String transactionId;
    private String walletId;
    private String partnerName;
    private String direction;
    private Double amount;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private String type;
    private String referenceId;
    private Boolean success;
    private Integer userId;
}
