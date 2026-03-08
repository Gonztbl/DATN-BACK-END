package com.vti.springdatajpa.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class ProductUpdateRequestDTO {
    
    private String name;
    
    private String description;
    
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private Double price;
    
    private Integer categoryId;
    
    private String restaurantId;
    
    private String status;
    
    private String imageBase64;
}
