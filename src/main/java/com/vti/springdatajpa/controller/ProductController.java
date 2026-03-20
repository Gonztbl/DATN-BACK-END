package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.ProductDetailDTO;
import com.vti.springdatajpa.dto.ProductPageResponseDTO;
import com.vti.springdatajpa.dto.ReviewResponseDTO;
import com.vti.springdatajpa.service.ProductService;
import com.vti.springdatajpa.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ProductPageResponseDTO> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        ProductPageResponseDTO response = productService.getProducts(page, limit, search, categoryId, restaurantId, status, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Integer id) {
        ProductDetailDTO dto = productService.getProductById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getProductReviews(@PathVariable Integer id) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByProductId(id);
        return ResponseEntity.ok(reviews);
    }
}
