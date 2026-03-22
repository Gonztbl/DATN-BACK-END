package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminOrderResponseDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String fullName;
    private String restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String recipientName;
    private String recipientPhone;
    private String deliveryAddress;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer shipperId;
}
