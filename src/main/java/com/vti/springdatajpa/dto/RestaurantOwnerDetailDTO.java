package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOwnerDetailDTO {
    private Integer id;
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private List<RestaurantOverviewDTO> restaurants;

    @Data
    @AllArgsConstructor
    public static class RestaurantOverviewDTO {
        private String id;
        private String name;
        private boolean status;
        private Integer productCount;
    }
}
