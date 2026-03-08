package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDetailDTO {
    private Integer id;
    private String name;
    private String icon;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
