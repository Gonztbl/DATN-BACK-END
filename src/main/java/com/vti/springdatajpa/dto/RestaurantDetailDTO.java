package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantDetailDTO {
    
    private String id;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    private String address;
    
    private String logoBase64;
    
    private Boolean status;
    
    private Integer productCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
