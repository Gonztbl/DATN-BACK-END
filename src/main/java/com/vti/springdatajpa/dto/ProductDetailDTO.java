package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDetailDTO {
    
    private Integer id;
    
    private String name;
    
    private String description;
    
    private Double price;
    
    private CategoryDTO category;
    
    private RestaurantDTO restaurant;
    
    private String imageBase64;
    
    private String status;
    
    private BigDecimal ratingAvg;
    
    private Integer ratingCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Data
    public static class CategoryDTO {
        private Integer id;
        private String name;
        private String icon;
    }
    
    @Data
    public static class RestaurantDTO {
        private String id;
        private String name;
        private String logoBase64;
    }
}
