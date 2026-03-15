package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.SupportTicketRequestDTO;
import com.vti.springdatajpa.dto.SupportTicketResponseDTO;
import com.vti.springdatajpa.entity.SupportTicket;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.SupportTicketRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    /**
     * POST /api/support/tickets - Create support ticket
     */
    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketResponseDTO> createTicket(@RequestBody SupportTicketRequestDTO request) {
        Integer userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());
        ticket.setOrderId(request.getOrderId());
        ticket.setAttachments(request.getAttachments());
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());

        SupportTicket saved = supportTicketRepository.save(ticket);

        SupportTicketResponseDTO response = new SupportTicketResponseDTO();
        response.setTicketId(saved.getId());
        response.setStatus(saved.getStatus().name());
        response.setCreatedAt(saved.getCreatedAt());
        response.setMessage("Yêu cầu hỗ trợ đã được gửi. Chúng tôi sẽ phản hồi trong 24h.");

        return ResponseEntity.status(201).body(response);
    }

    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type");
        }

        return userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
