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
public class LoanRequestResponseDTO {

    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String purpose;
    private BigDecimal declaredIncome;
    private String jobSegmentNum;
    private Double aiScore;
    private String aiDecision;
    private String adminNote;
    private LoanStatus finalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields (optional, for frontend convenience)
    private String statusDisplay;
    private String descriptionText;
}
