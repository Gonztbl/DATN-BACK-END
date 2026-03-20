package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.OwnerStatisticsDTO;
import com.vti.springdatajpa.dto.RestaurantOwnerDetailDTO;
import com.vti.springdatajpa.service.AdminRestaurantOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurant-owners")
@RequiredArgsConstructor
public class AdminRestaurantOwnerController {
    
    private final AdminRestaurantOwnerService adminRestaurantOwnerService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPPORT')")
    public ResponseEntity<org.springframework.data.domain.Page<RestaurantOwnerDetailDTO>> getRestaurantOwners(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(adminRestaurantOwnerService.getRestaurantOwners(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPPORT')")
    public ResponseEntity<RestaurantOwnerDetailDTO> getOwnerDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminRestaurantOwnerService.getOwnerDetail(id));
    }

    @GetMapping("/{id}/restaurants")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPPORT')")
    public ResponseEntity<List<RestaurantOwnerDetailDTO.RestaurantOverviewDTO>> getOwnerRestaurants(@PathVariable Integer id) {
        return ResponseEntity.ok(adminRestaurantOwnerService.getOwnerRestaurants(id));
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPPORT')")
    public ResponseEntity<OwnerStatisticsDTO> getOwnerStatistics(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(adminRestaurantOwnerService.getOwnerStatistics(id, fromDate, toDate));
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> lockOwner(@PathVariable Integer id) {
        adminRestaurantOwnerService.lockRestaurantOwner(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> unlockOwner(@PathVariable Integer id) {
        adminRestaurantOwnerService.unlockRestaurantOwner(id);
        return ResponseEntity.ok().build();
    }
}
