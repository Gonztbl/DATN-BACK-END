package com.vti.springdatajpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupportTicketRequestDTO {
    private String subject;
    private String message;
    private Integer orderId;
    private String attachments; // JSON array of base64 strings
}
