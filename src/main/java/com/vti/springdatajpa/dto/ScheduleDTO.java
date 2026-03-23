package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private String day;
    private String label;
    private Boolean isOpen;
    private String openTime;
    private String closeTime;
}
