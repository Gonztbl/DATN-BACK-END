package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestApplyDTO {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000000", message = "Minimum loan amount is 1,000,000 VND")
    @DecimalMax(value = "1000000000", message = "Maximum loan amount is 1,000,000,000 VND")
    private BigDecimal amount;

    @NotNull(message = "Loan term is required")
    @Min(value = 3, message = "Minimum term is 3 months")
    @Max(value = 60, message = "Maximum term is 60 months")
    private Integer term;

    @NotBlank(message = "Loan purpose is required")
    @Size(min = 5, max = 255, message = "Purpose must be between 5 and 255 characters")
    private String purpose;

    @NotNull(message = "Declared income is required")
    @DecimalMin(value = "0", message = "Declared income must be positive")
    private BigDecimal declaredIncome;

    @NotBlank(message = "Job segment is required")
    private String jobSegmentNum;
}
