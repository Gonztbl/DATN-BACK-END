package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private String id;
    private UserDto user;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
