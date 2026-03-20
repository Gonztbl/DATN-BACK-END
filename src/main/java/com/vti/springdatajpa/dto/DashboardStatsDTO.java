package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalUsers;
    private long newUsersToday;
    private long totalWallets;
    private long activeWallets;
    private long totalTransactions;
    private long transactionsToday;
    private BigDecimal totalRevenue;
    private BigDecimal revenueToday;
    private long totalOrders;
    private long ordersToday;
}
