package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLogDTO {
    private Integer id;
    private Integer adminId;
    private String adminName;
    private String actionType;
    private String targetType;
    private Integer targetId;
    private String reason;
    private String metadata;
    private LocalDateTime createdAt;
}
