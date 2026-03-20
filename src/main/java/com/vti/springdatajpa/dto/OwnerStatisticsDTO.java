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
public class OwnerStatisticsDTO {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private BigDecimal avgOrderValue;
    private List<RestaurantStatDTO> restaurants;

    @Data
    @AllArgsConstructor
    public static class RestaurantStatDTO {
        private String restaurantId;
        private String name;
        private BigDecimal revenue;
        private long orders;
    }
}
