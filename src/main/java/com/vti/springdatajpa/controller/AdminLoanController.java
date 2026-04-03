package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminLoanDetailDTO;
import com.vti.springdatajpa.dto.LoanRequestResponseDTO;
import com.vti.springdatajpa.entity.LoanRequest;
import com.vti.springdatajpa.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/loans")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin Loan APIs", description = "Admin loan management endpoints")
public class AdminLoanController {

    private final LoanService loanService;

    @io.swagger.v3.oas.annotations.Operation(summary = "List pending loans", description = "Get loans with status PENDING_ADMIN")
    @GetMapping
    public ResponseEntity<Page<LoanRequest>> getPendingLoans(Pageable pageable) {
        Page<LoanRequest> page = loanService.getPendingAdminReviewLoans(pageable);
        return ResponseEntity.ok(page);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get loan info", description = "Get full loan + user + AI info for admin")
    @GetMapping("/{id}")
    public ResponseEntity<AdminLoanDetailDTO> getLoanDetails(@PathVariable("id") Long loanId) {
        AdminLoanDetailDTO detail = loanService.getAdminLoanDetail(loanId);
        return ResponseEntity.ok(detail);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Approve loan", description = "Admin approves loan and disburses money")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanRequestResponseDTO> approveLoan(
            @PathVariable("id") Long loanId,
            @RequestBody Map<String, String> body
    ) {
        String adminNote = body.getOrDefault("adminNote", "Duyệt khoản vay");
        LoanRequestResponseDTO response = loanService.approveLoan(loanId, adminNote);
        return ResponseEntity.ok(response);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Reject loan", description = "Admin rejects loan and sets admin note")
    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanRequestResponseDTO> rejectLoan(
            @PathVariable("id") Long loanId,
            @RequestBody Map<String, String> body
    ) {
        String adminNote = body.getOrDefault("adminNote", "Từ chối khoản vay");
        LoanRequestResponseDTO response = loanService.rejectLoan(loanId, adminNote);
        return ResponseEntity.ok(response);
    }
}
