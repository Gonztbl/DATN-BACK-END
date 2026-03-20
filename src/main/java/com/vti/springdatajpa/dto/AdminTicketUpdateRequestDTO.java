package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.SupportTicket.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTicketUpdateRequestDTO {
    private String replyMessage;
    private TicketStatus status;
    private Integer assigneeId;
}
