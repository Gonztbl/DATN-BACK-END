package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminQRCodeDTO;
import com.vti.springdatajpa.service.AdminQRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminQRCodeController {

    private final AdminQRCodeService adminQRCodeService;

    @GetMapping("/users/{userId}/qrcodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminQRCodeDTO>> getUserQRCodes(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminQRCodeService.getUserQRCodes(userId));
    }

    @DeleteMapping("/qrcodes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQRCode(@PathVariable Integer id) {
        adminQRCodeService.deleteQRCode(id);
        return ResponseEntity.noContent().build();
    }
}
