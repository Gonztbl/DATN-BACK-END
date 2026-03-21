package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.SystemConfigDTO;
import com.vti.springdatajpa.service.AdminSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemConfigDTO>> getSettings() {
        return ResponseEntity.ok(adminSettingsService.getAllSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemConfigDTO>> updateSettings(@RequestBody List<SystemConfigDTO> settings) {
        return ResponseEntity.ok(adminSettingsService.updateSettings(settings));
    }
}
