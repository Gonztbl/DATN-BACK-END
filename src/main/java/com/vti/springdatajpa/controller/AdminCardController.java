package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminCardDTO;
import com.vti.springdatajpa.entity.enums.BankAccountStatus;
import com.vti.springdatajpa.service.AdminCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping("/users/{userId}/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminCardDTO>> getUserCards(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminCardService.getUserCards(userId));
    }

    @GetMapping("/cards/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCardDTO> getCardDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminCardService.getCardDetail(id));
    }

    @PutMapping("/cards/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCardDTO> lockCard(@PathVariable Integer id) {
        return ResponseEntity.ok(adminCardService.updateCardStatus(id, BankAccountStatus.REVOKED));
    }

    @PutMapping("/cards/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCardDTO> unlockCard(@PathVariable Integer id) {
        return ResponseEntity.ok(adminCardService.updateCardStatus(id, BankAccountStatus.ACTIVE));
    }
}
