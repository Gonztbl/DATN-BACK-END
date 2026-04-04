package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestListDTO {
    private Long loanId;
    private BigDecimal amount;
    private Double aiScore;
    private LoanStatus finalStatus;
    private Integer userId;
    private String fullName;
    private LocalDateTime createdAt;
}
