package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewRequestDTO {
    private Integer rating;
    private String comment;
}
