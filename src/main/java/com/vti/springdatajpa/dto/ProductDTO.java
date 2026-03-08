package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private CategoryDTO category;
    private BigDecimal price;
    private BigDecimal rating;
    private String imageUrl;
    private String restaurantId;
    private String restaurantName;
}
