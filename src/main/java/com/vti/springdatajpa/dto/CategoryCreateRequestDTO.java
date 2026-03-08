package com.vti.springdatajpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryCreateRequestDTO {
    
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    
    private String icon;
    
    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer orderIndex;
}
