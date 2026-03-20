package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.CategoryDetailDTO;
import com.vti.springdatajpa.dto.CategoryPageResponseDTO;
import com.vti.springdatajpa.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Public API", description = "Public endpoints for categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a full list of categories for public use")
    public ResponseEntity<List<CategoryDetailDTO>> getAllCategories() {
        // Use a large limit to get all categories at once as requested
        CategoryPageResponseDTO response = categoryService.getCategories(1, 1000, null, "id", "asc");
        return ResponseEntity.ok(response.getContent());
    }
}
