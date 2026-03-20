package com.vti.springdatajpa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    @JsonProperty("user_id")
    private String userId;
    
    private List<OrderItemRequestDTO> items;
    
    @JsonProperty("delivery_address")
    private String deliveryAddress;
    
    @JsonProperty("recipient_name")
    private String recipientName;
    
    @JsonProperty("recipient_phone")
    private String recipientPhone;
    
    private String note;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("restaurant_id")
    private String restaurantId;
    
    @JsonProperty("total_amount")
    private String totalAmount;

    @Data
    public static class OrderItemRequestDTO {
        @JsonProperty("product_id")
        private String productId;
        
        private Integer quantity;
        
        @JsonProperty("price_at_time")
        private BigDecimal priceAtTime;
    }
}
