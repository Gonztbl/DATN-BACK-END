package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.AdminTicketUpdateRequestDTO;
import com.vti.springdatajpa.dto.SupportTicketDTO;
import com.vti.springdatajpa.dto.TicketReplyDTO;
import com.vti.springdatajpa.entity.SupportTicket;
import com.vti.springdatajpa.entity.SupportTicket.TicketStatus;
import com.vti.springdatajpa.entity.TicketReply;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.SupportTicketRepository;
import com.vti.springdatajpa.repository.TicketReplyRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final TicketReplyRepository ticketReplyRepository;
    private final UserRepository userRepository;

    public Page<SupportTicketDTO> getAllTickets(String statusString, Pageable pageable) {
        Page<SupportTicket> page;
        if (statusString != null && !statusString.isBlank()) {
            try {
                TicketStatus status = TicketStatus.valueOf(statusString.toUpperCase());
                page = supportTicketRepository.findByStatus(status, pageable);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket status");
            }
        } else {
            page = supportTicketRepository.findAll(pageable);
        }

        return page.map(this::mapToDTO);
    }

    public SupportTicketDTO getTicketDetail(Integer id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        return mapToDTO(ticket);
    }

    @Transactional
    public SupportTicketDTO replyToTicket(Integer id, Integer adminId, AdminTicketUpdateRequestDTO request) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (request.getReplyMessage() == null || request.getReplyMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reply message cannot be empty");
        }

        TicketReply reply = TicketReply.builder()
                .ticket(ticket)
                .adminId(adminId)
                .message(request.getReplyMessage())
                .build();
        ticketReplyRepository.save(reply);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }
        
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        return mapToDTO(ticket);
    }

    @Transactional
    public SupportTicketDTO updateTicketStatus(Integer id, AdminTicketUpdateRequestDTO request) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        ticket.setStatus(request.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        return mapToDTO(ticket);
    }

    @Transactional
    public SupportTicketDTO assignTicket(Integer id, AdminTicketUpdateRequestDTO request) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (request.getAssigneeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee ID is required");
        }

        User admin = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));

        ticket.setAssignedTo(admin.getId());
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        return mapToDTO(ticket);
    }

    private SupportTicketDTO mapToDTO(SupportTicket ticket) {
        List<TicketReplyDTO> replies = (ticket.getReplies() != null) ? ticket.getReplies().stream().map(r -> {
            String adminName = userRepository.findById(r.getAdminId()).map(User::getFullName).orElse("Unknown Admin");
            return TicketReplyDTO.builder()
                    .id(r.getId())
                    .ticketId(ticket.getId())
                    .adminId(r.getAdminId())
                    .adminName(adminName)
                    .message(r.getMessage())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).collect(Collectors.toList()) : List.of();

        String assigneeName = null;
        if (ticket.getAssignedTo() != null) {
            assigneeName = userRepository.findById(ticket.getAssignedTo()).map(User::getFullName).orElse(null);
        }

        return SupportTicketDTO.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .userName(ticket.getUser().getFullName() != null ? ticket.getUser().getFullName() : ticket.getUser().getUserName())
                .subject(ticket.getSubject())
                .message(ticket.getMessage())
                .orderId(ticket.getOrderId())
                .attachments(ticket.getAttachments())
                .status(ticket.getStatus())
                .assignedTo(ticket.getAssignedTo())
                .assignedToName(assigneeName)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .replies(replies)
                .build();
    }
}
