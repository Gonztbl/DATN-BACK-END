package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteResponseDTO {
    private Integer id;
    private String restaurantId;
    private String restaurantName;
    private String logoBase64;
    private String address;
    private LocalDateTime favoritedAt;
}
