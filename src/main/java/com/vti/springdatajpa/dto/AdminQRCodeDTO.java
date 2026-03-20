package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.enums.QRType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminQRCodeDTO {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private Integer walletId;
    private String codeValue;
    private QRType type;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
