package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class CategoryUpdateRequestDTO {
    
    private String name;
    
    private String icon;
    
    private Integer orderIndex;
}
