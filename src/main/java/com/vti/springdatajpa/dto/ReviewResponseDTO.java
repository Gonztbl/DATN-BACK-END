package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String productName;
}
