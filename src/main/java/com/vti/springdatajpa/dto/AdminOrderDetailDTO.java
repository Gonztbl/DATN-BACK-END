package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminOrderDetailDTO {
    private Integer id;
    private AdminOrderUserDTO user;
    private AdminOrderRestaurantDTO restaurant;
    private List<AdminOrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private List<AdminOrderStatusHistoryDTO> statusHistory;
    private AdminOrderPaymentDTO payment;
    private AdminOrderDeliveryDTO deliveryInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer shipperId;

    @Data
    public static class AdminOrderUserDTO {
        private Integer id;
        private String userName;
        private String fullName;
        private String phone;
        private String email;
    }

    @Data
    public static class AdminOrderRestaurantDTO {
        private String id;
        private String name;
        private String phone;
        private String address;
    }

    @Data
    public static class AdminOrderItemDTO {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtTime;
        private BigDecimal subtotal;
        private String image;
    }

    @Data
    public static class AdminOrderStatusHistoryDTO {
        private String status;
        private LocalDateTime updatedAt;
        private String updatedBy;
    }

    @Data
    public static class AdminOrderPaymentDTO {
        private String method;
        private Integer transactionId;
        private BigDecimal amount;
        private String status;
        private LocalDateTime paidAt;
    }

    @Data
    public static class AdminOrderDeliveryDTO {
        private String recipientName;
        private String recipientPhone;
        private String deliveryAddress;
        private String note;
    }
}
