package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    private String userId;
    private List<OrderItemRequestDTO> items;
    private String deliveryAddress;
    private String recipientName;
    private String recipientPhone;
    private String note;
    private String paymentMethod;

    @Data
    public static class OrderItemRequestDTO {
        private String productId;
        private Integer quantity;
        private BigDecimal price;
    }
}
