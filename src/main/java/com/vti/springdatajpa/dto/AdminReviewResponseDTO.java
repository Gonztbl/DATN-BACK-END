package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminReviewResponseDTO {
    private Integer id;
    private AdminReviewUserDTO user;
    private AdminReviewProductDTO product;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    @Data
    public static class AdminReviewUserDTO {
        private Integer id;
        private String fullName;
        private String avatarUrl;
    }

    @Data
    public static class AdminReviewProductDTO {
        private Integer id;
        private String name;
    }
}
