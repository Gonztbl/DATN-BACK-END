package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.SupportTicket.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String subject;
    private String message;
    private Integer orderId;
    private String attachments;
    private TicketStatus status;
    private Integer assignedTo;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TicketReplyDTO> replies;
}
