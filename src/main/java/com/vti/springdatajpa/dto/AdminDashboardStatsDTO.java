package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDTO {
    private Long totalPending;
    private Double averageAmount;
    private Long highRiskCount;
    private Long moderateRiskCount;
    private Long lowRiskCount;
}
