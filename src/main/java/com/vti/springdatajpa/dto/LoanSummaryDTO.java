package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanSummaryDTO {
    private Integer totalLoans;
    private BigDecimal totalLoanAmount;
    private Integer approvedLoans;
    private Integer pendingAdminLoans;
    private Integer rejectedLoans;
    private Double averageAiScore;
    private String creditRating;
    private String statusDisplay;
    private String descriptionText;
}
