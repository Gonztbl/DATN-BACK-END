package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private String id;
    private String name;
    private String address;
    private String logoUrl;
    private boolean isOpen;
}
