package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.LoanRequestApplyDTO;
import com.vti.springdatajpa.dto.LoanRequestResponseDTO;
import com.vti.springdatajpa.dto.LoanSummaryDTO;
import com.vti.springdatajpa.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Loan APIs", description = "APIs for loan application and customer loan queries")
public class LoanController {

    private final LoanService loanService;

    @io.swagger.v3.oas.annotations.Operation(summary = "Apply for loan", description = "Submit loan application form and run AI scoring")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application processed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/apply")
    public ResponseEntity<LoanRequestResponseDTO> applyForLoan(@Valid @RequestBody LoanRequestApplyDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find user by username (based on existing user service or repository)
        // We assume currently authenticated username = email or userName;
        // Due to existing app structure, you may need to adapt by actual user identity object.
        
        // This controller expects the UserService or Security principal to expose ID reliably.
        // Here we'll use a helper from loanService (not implemented) for simplicity.

        Integer userId = loanService.getAuthenticatedUserId();
        LoanRequestResponseDTO response = loanService.applyForLoan(userId, request);
        return ResponseEntity.ok(response);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get my loans", description = "Retrieve logged-in user's loan requests (paginated)")
    @GetMapping("/my-loans")
    public ResponseEntity<Page<LoanRequestResponseDTO>> getMyLoans(Pageable pageable) {
        Integer userId = loanService.getAuthenticatedUserId();
        Page<LoanRequestResponseDTO> page = loanService.getMyLoans(userId, pageable);
        return ResponseEntity.ok(page);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Loan summary", description = "Retrieve loan summary for current user dashboard")
    @GetMapping("/summary")
    public ResponseEntity<LoanSummaryDTO> getLoanSummary() {
        Integer userId = loanService.getAuthenticatedUserId();
        LoanSummaryDTO summary = loanService.getLoanSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get loan detail", description = "Get detail of one loan for current user")
    @GetMapping("/{id}")
    public ResponseEntity<LoanRequestResponseDTO> getLoanById(@PathVariable("id") Long loanId) {
        Integer userId = loanService.getAuthenticatedUserId();
        LoanRequestResponseDTO response = loanService.getLoanById(loanId, userId);
        return ResponseEntity.ok(response);
    }
}
