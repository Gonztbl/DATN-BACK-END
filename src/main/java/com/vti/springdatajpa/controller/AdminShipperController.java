package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.ShipperDetailDTO;
import com.vti.springdatajpa.dto.ShipperStatisticsDTO;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.service.AdminShipperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/shippers")
@RequiredArgsConstructor
public class AdminShipperController {
    
    private final AdminShipperService adminShipperService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<org.springframework.data.domain.Page<ShipperDetailDTO>> getShippers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isOnline,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(adminShipperService.getShippers(search, isOnline, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ShipperDetailDTO> getShipperDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminShipperService.getShipperDetail(id));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<Page<Order>> getShipperOrders(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminShipperService.getShipperOrders(id, status, fromDate, toDate, pageable));
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ShipperStatisticsDTO> getShipperStatistics(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(adminShipperService.getShipperStatistics(id, fromDate, toDate));
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> lockShipper(@PathVariable Integer id) {
        adminShipperService.lockShipper(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlockShipper(@PathVariable Integer id) {
        adminShipperService.unlockShipper(id);
        return ResponseEntity.ok().build();
    }
}
