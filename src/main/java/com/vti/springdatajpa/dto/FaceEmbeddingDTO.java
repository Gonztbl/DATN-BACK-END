package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceEmbeddingDTO {
    private Long id;
    private Integer userId;
    private String pose;
    private LocalDateTime createdAt;
    // Note: embedding vector is NOT exposed in the DTO for security
}
