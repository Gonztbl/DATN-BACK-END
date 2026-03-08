package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private String orderId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
