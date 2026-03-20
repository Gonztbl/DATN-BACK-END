package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class RestaurantUpdateRequestDTO {
    
    private String name;
    
    private String phone;
    
    private String email;
    
    private String address;
    
    private String logoBase64;
    
    private Boolean status;
    
    private String description;
    
    private Integer categoryId;
}
