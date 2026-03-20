package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperStatisticsDTO {
    private long totalOrders;
    private long completedOrders;
    private long failedOrders;
    private BigDecimal totalEarnings;
    private double avgRating;
    private List<DailyShipperStatsDTO> dailyStats;
}
