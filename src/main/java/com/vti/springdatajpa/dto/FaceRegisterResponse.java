package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceRegisterResponse {
    private Long embeddingId;
    private Integer userId;
    private String pose;
    private String message;
}
