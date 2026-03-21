package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminTicketUpdateRequestDTO;
import com.vti.springdatajpa.dto.SupportTicketDTO;
import com.vti.springdatajpa.service.AdminSupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/support-tickets")
@RequiredArgsConstructor
public class AdminSupportTicketController {

    private final AdminSupportTicketService adminSupportTicketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<Page<SupportTicketDTO>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminSupportTicketService.getAllTickets(status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<SupportTicketDTO> getTicketDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminSupportTicketService.getTicketDetail(id));
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<SupportTicketDTO> replyToTicket(
            @PathVariable Integer id,
            @RequestBody AdminTicketUpdateRequestDTO request) {
        // Assume admin ID is 1 for now if we can't get it from SecurityContext easily
        // Usually: (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer adminId = 1; // You can pull from auth context
        return ResponseEntity.ok(adminSupportTicketService.replyToTicket(id, adminId, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<SupportTicketDTO> updateTicketStatus(
            @PathVariable Integer id,
            @RequestBody AdminTicketUpdateRequestDTO request) {
        return ResponseEntity.ok(adminSupportTicketService.updateTicketStatus(id, request));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<SupportTicketDTO> assignTicket(
            @PathVariable Integer id,
            @RequestBody AdminTicketUpdateRequestDTO request) {
        return ResponseEntity.ok(adminSupportTicketService.assignTicket(id, request));
    }
}
