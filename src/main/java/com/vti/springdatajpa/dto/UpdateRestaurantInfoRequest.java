package com.vti.springdatajpa.dto;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateRestaurantInfoRequest {
    @NotBlank(message = "Tên nhà hàng không được để trống")
    private String name;
    
    private String phone;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    
    @NotNull(message = "Trạng thái là bắt buộc")
    private String status;

    private List<ScheduleDTO> schedule;
}
