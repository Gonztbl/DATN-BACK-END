package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupportTicketResponseDTO {
    private Integer ticketId;
    private String status;
    private LocalDateTime createdAt;
    private String message;
}
