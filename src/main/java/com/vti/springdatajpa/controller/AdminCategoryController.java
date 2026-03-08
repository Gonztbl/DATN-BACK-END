package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.service.CategoryService;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin Category Management", description = "APIs for managing categories by admin")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get categories with pagination, search, and sort", description = "Retrieve paginated list of categories with search and sorting capabilities")
    public ResponseEntity<CategoryPageResponseDTO> getCategories(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Search term for name or ID") @RequestParam(required = false) String search,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        CategoryPageResponseDTO response = categoryService.getCategories(page, limit, search, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve detailed information of a specific category")
    public ResponseEntity<CategoryDetailDTO> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Integer id) {
        
        CategoryDetailDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new category with name, icon, and order")
    public ResponseEntity<CategoryDetailDTO> createCategory(
            @Parameter(description = "Category creation request") @Valid @RequestBody CategoryCreateRequestDTO request) {
        
        CategoryDetailDTO createdCategory = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category's information")
    public ResponseEntity<CategoryDetailDTO> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Integer id,
            @Parameter(description = "Category update request") @Valid @RequestBody CategoryUpdateRequestDTO request) {
        
        CategoryDetailDTO updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category (only if no products are associated)")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Integer id) {
        
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-name")
    @Operation(summary = "Check if category name exists", description = "Verify if a category name already exists (excluding a specific ID when updating)")
    public ResponseEntity<CategoryCheckNameDTO> checkCategoryName(
            @Parameter(description = "Category name to check") @RequestParam String name,
            @Parameter(description = "Exclude this ID from check (for update scenarios)") @RequestParam(required = false) Integer excludeId) {
        
        CategoryCheckNameDTO response = categoryService.checkCategoryName(name, excludeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @Operation(summary = "Export categories to Excel", description = "Export all categories to an Excel file")
    public ResponseEntity<ByteArrayResource> exportCategories() throws IOException {
        // Get all categories for export
        CategoryPageResponseDTO allCategories = categoryService.getCategories(0, 1000, null, "id", "asc");
        
        // Create Excel file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        createExcelFile(allCategories.getContent(), outputStream);
        
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(outputStream.size())
                .body(resource);
    }

    private void createExcelFile(List<CategoryDetailDTO> categories, ByteArrayOutputStream outputStream) throws IOException {
        // Simple CSV format for now (can be enhanced with proper Excel library like Apache POI)
        StringBuilder csvContent = new StringBuilder();
        
        // Header
        csvContent.append("ID,Name,Icon,Order Index,Created At,Updated At\n");
        
        // Data rows
        for (CategoryDetailDTO category : categories) {
            csvContent.append(category.getId()).append(",");
            csvContent.append("\"").append(category.getName()).append("\",");
            csvContent.append("\"").append(category.getIcon() != null ? category.getIcon() : "").append("\",");
            csvContent.append(category.getOrderIndex()).append(",");
            csvContent.append("\"").append(category.getCreatedAt()).append("\",");
            csvContent.append("\"").append(category.getUpdatedAt()).append("\"");
            csvContent.append("\n");
        }
        
        outputStream.write(csvContent.toString().getBytes());
    }
}
