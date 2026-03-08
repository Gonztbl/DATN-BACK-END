package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Product Management", description = "APIs for managing products by admin")
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get products with pagination, search, and filters", description = "Retrieve paginated list of products with search, category, restaurant, status filters, and sorting")
    public ResponseEntity<ProductPageResponseDTO> getProducts(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Search term for name or description") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "Filter by restaurant ID") @RequestParam(required = false) String restaurantId,
            @Parameter(description = "Filter by status (available, unavailable)") @RequestParam(required = false) String status,
            @Parameter(description = "Sort field (name, price, created_at, rating_avg)") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        ProductPageResponseDTO response = productService.getProducts(page, limit, search, categoryId, restaurantId, status, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve detailed information of a specific product")
    public ResponseEntity<ProductDetailDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Integer id) {
        
        ProductDetailDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product with details and optional image")
    public ResponseEntity<ProductDetailDTO> createProduct(
            @Parameter(description = "Product creation request") @Valid @RequestBody ProductCreateRequestDTO request) {
        
        ProductDetailDTO createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product's information")
    public ResponseEntity<ProductDetailDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Integer id,
            @Parameter(description = "Product update request") @Valid @RequestBody ProductUpdateRequestDTO request) {
        
        ProductDetailDTO updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete product", description = "Delete a product (soft delete)")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable(name = "id") String id) {
        
        System.out.println("DELETE request received for product ID (String): " + id);
        
        try {
            Integer productId = Integer.parseInt(id);
            System.out.println("Parsed product ID (Integer): " + productId);
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format: " + id);
            return ResponseEntity.badRequest().build();
        }
    }
}
