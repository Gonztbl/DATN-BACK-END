package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminDashboardStatsDTO;
import com.vti.springdatajpa.dto.AdminLoanDetailDTO;
import com.vti.springdatajpa.dto.LoanRequestListDTO;
import com.vti.springdatajpa.dto.LoanRequestResponseDTO;
import com.vti.springdatajpa.entity.enums.LoanStatus;
import com.vti.springdatajpa.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/loans")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin Loan APIs", description = "Admin loan management endpoints")
public class AdminLoanController {

    private final LoanService loanService;

    @io.swagger.v3.oas.annotations.Operation(summary = "List pending loans", description = "Get loans with status PENDING_ADMIN")
    @GetMapping
    public ResponseEntity<Page<LoanRequestListDTO>> getPendingLoans(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minAiScore,
            @RequestParam(required = false) Double maxAiScore,
            Pageable pageable
    ) {
        try {
            Page<LoanRequestListDTO> page = loanService.getPendingAdminLoanList(keyword, minAiScore, maxAiScore, pageable);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Page.empty());
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get loan info", description = "Get full loan + user + AI info for admin")
    @GetMapping("/{id}")
    public ResponseEntity<AdminLoanDetailDTO> getLoanDetails(@PathVariable("id") Long loanId) {
        try {
            AdminLoanDetailDTO detail = loanService.getAdminLoanDetail(loanId);
            return ResponseEntity.ok(detail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get dashboard stats", description = "Get pending loan stats for admin dashboard")
    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getLoanStats() {
        try {
            AdminDashboardStatsDTO stats = loanService.getAdminDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Approve loan", description = "Admin approves loan and disburses money")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanRequestResponseDTO> approveLoan(
            @PathVariable("id") Long loanId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String adminNote = body.getOrDefault("adminNote", "Duyệt khoản vay");
            LoanRequestResponseDTO response = loanService.approveLoan(loanId, adminNote);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Reject loan", description = "Admin rejects loan and sets admin note")
    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanRequestResponseDTO> rejectLoan(
            @PathVariable("id") Long loanId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String adminNote = body.getOrDefault("adminNote", "Từ chối khoản vay");
            LoanRequestResponseDTO response = loanService.rejectLoan(loanId, adminNote);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get all loans", description = "Get all loans with optional filters. If status is not provided, returns all loans regardless of status.")
    @GetMapping("/all")
    public ResponseEntity<Page<LoanRequestListDTO>> getAllLoans(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minAiScore,
            @RequestParam(required = false) Double maxAiScore,
            @RequestParam(required = false) LoanStatus status,
            Pageable pageable
    ) {
        try {
            Page<LoanRequestListDTO> page = loanService.getAllLoansList(keyword, minAiScore, maxAiScore, status, pageable);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Page.empty());
        }
    }
}
