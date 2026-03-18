package com.vti.springdatajpa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse {
    private String message;
    
    public ApiResponse(String message) {
        this.message = message;
    }
}
