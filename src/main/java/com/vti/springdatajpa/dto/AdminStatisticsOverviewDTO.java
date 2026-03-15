package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@Data
public class AdminStatisticsOverviewDTO {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Map<String, Long> ordersByStatus;
    private BigDecimal revenueToday;
    private Integer ordersToday;
    private Integer newUsersToday;
    private List<TopRestaurantDTO> topRestaurants;

    @Data
    public static class TopRestaurantDTO {
        private String restaurantId;
        private String restaurantName;
        private Long orderCount;
        private BigDecimal revenue;
    }
}
