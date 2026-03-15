package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class SetDefaultCardResponseDTO {
    private Integer id;
    private String cardNumber;
    private boolean isDefault;
    private String message;
}
