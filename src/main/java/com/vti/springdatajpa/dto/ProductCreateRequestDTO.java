package com.vti.springdatajpa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequestDTO {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private Double price;
    
    @NotNull(message = "Category ID is required")
    private Integer categoryId;
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    private String status = "available";
    
    private String imageBase64;
}
