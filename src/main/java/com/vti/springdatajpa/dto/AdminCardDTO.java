package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.enums.BankAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCardDTO {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountName;
    private BankAccountStatus status;
    private LocalDateTime createdAt;
}
