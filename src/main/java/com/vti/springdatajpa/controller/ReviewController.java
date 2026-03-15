package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.ReviewRequestDTO;
import com.vti.springdatajpa.dto.ReviewResponseDTO;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.entity.Review;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.ReviewRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * POST /api/products/{id}/reviews - Submit review for product
     */
    @PostMapping("/products/{id}/reviews")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Integer id,
            @RequestBody ReviewRequestDTO request) {
        Integer userId = getCurrentUserId();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if user already reviewed this product
        Review existingReview = reviewRepository.findByUserId(userId).stream()
                .filter(r -> r.getProductId().equals(id))
                .findFirst()
                .orElse(null);

        Review review;
        if (existingReview != null) {
            // Update existing review
            existingReview.setRating(request.getRating());
            existingReview.setComment(request.getComment());
            review = reviewRepository.save(existingReview);
        } else {
            // Create new review
            review = new Review();
            review.setUserId(userId);
            review.setProductId(id);
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setCreatedAt(LocalDateTime.now());
            review = reviewRepository.save(review);
        }

        return ResponseEntity.status(201).body(mapToDTO(review, product));
    }

    /**
     * PUT /api/reviews/{id} - Update review
     */
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Integer id,
            @RequestBody ReviewRequestDTO request) {
        Integer userId = getCurrentUserId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: Not your review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review saved = reviewRepository.save(review);

        Product product = productRepository.findById(review.getProductId()).orElse(null);
        return ResponseEntity.ok(mapToDTO(saved, product));
    }

    /**
     * DELETE /api/reviews/{id} - Delete review (user)
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: Not your review");
        }

        reviewRepository.delete(review);
        return ResponseEntity.noContent().build();
    }

    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type");
        }

        return userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ReviewResponseDTO mapToDTO(Review review, Product product) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setUserId(review.getUserId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        if (product != null) {
            dto.setProductName(product.getName());
        }
        return dto;
    }
}
