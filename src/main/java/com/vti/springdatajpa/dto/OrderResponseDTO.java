package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Integer id;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String recipientName;
    private String recipientPhone;
    private String deliveryAddress;
    private String note;
    private String paymentMethod;
    private Integer itemCount;
    private List<OrderItemDTO> items;

    // For detail endpoint
    private List<PaymentHistoryDTO> paymentHistory;
    private List<StatusHistoryDTO> statusHistory;

    @Data
    public static class OrderItemDTO {
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private BigDecimal priceAtTime;
        private BigDecimal subtotal;
    }

    @Data
    public static class PaymentHistoryDTO {
        private Integer transactionId;
        private BigDecimal amount;
        private String status;
        private String paymentMethod;
        private LocalDateTime timestamp;
        private String referenceId;
    }

    @Data
    public static class StatusHistoryDTO {
        private String status;
        private LocalDateTime timestamp;
        private String note;
    }
}
