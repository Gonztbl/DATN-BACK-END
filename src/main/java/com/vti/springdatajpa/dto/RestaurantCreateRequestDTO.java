package com.vti.springdatajpa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantCreateRequestDTO {
    
    @NotBlank(message = "Tên nhà hàng không được để trống")
    private String name;
    
    private String phone;
    
    private String email;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    
    private String logoBase64;
    
    private Boolean status = true;
}
