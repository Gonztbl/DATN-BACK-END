package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants")
@RequiredArgsConstructor
@Tag(name = "Admin Restaurant Management", description = "APIs for managing restaurants by admin")
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "Get restaurants with pagination, search, and filters", description = "Retrieve paginated list of restaurants with search, status filter, and sorting capabilities")
    public ResponseEntity<RestaurantPageResponseDTO> getRestaurants(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Search term for name, address, phone, email, or ID") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status (true=open, false=closed)") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Sort field (name, created_at, product_count)") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        RestaurantPageResponseDTO response = restaurantService.getRestaurants(page, limit, search, status, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieve detailed information of a specific restaurant")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantById(
            @Parameter(description = "Restaurant ID") @PathVariable String id) {
        
        RestaurantDetailDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @PostMapping
    @Operation(summary = "Create new restaurant", description = "Create a new restaurant with details and optional logo")
    public ResponseEntity<RestaurantDetailDTO> createRestaurant(
            @Parameter(description = "Restaurant creation request") @Valid @RequestBody RestaurantCreateRequestDTO request) {
        
        RestaurantDetailDTO createdRestaurant = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Update an existing restaurant's information")
    public ResponseEntity<RestaurantDetailDTO> updateRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable String id,
            @Parameter(description = "Restaurant update request") @Valid @RequestBody RestaurantUpdateRequestDTO request) {
        
        RestaurantDetailDTO updatedRestaurant = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Delete a restaurant (soft delete, only if no products are associated)")
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "Restaurant ID") @PathVariable String id) {
        
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-name")
    @Operation(summary = "Check if restaurant name exists", description = "Verify if a restaurant name already exists (excluding a specific ID when updating)")
    public ResponseEntity<RestaurantCheckNameDTO> checkRestaurantName(
            @Parameter(description = "Restaurant name to check") @RequestParam String name,
            @Parameter(description = "Exclude this ID from check (for update scenarios)") @RequestParam(required = false) String excludeId) {
        
        RestaurantCheckNameDTO response = restaurantService.checkRestaurantName(name, excludeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @Operation(summary = "Export restaurants to CSV", description = "Export all restaurants to a CSV file with optional filters")
    public ResponseEntity<ByteArrayResource> exportRestaurants(
            @Parameter(description = "Search term for filtering") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status (true=open, false=closed)") @RequestParam(required = false) Boolean status) throws Exception {
        
        byte[] csvData = restaurantService.exportRestaurants(search, status);
        
        ByteArrayResource resource = new ByteArrayResource(csvData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=restaurants.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvData.length)
                .body(resource);
    }
}
