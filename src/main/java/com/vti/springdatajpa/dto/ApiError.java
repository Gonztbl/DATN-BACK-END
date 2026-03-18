package com.vti.springdatajpa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiError {
    private String error;
    
    public ApiError(String error) {
        this.error = error;
    }
}
