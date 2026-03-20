package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperDetailDTO {
    private Integer id;
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private ShipperStatisticsDTO statistics;
}
