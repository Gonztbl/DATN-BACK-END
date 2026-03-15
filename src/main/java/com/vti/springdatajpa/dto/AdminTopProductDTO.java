package com.vti.springdatajpa.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminTopProductDTO {
    private Integer productId;
    private String productName;
    private Integer quantitySold;
    private BigDecimal revenue;
}
