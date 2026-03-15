package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminReviewResponseDTO;
import com.vti.springdatajpa.entity.Review;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.ReviewRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/admin/reviews - Get all reviews (admin only)
     */
    @GetMapping
    public ResponseEntity<Page<AdminReviewResponseDTO>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String fromDate) {

        verifyAdminAccess();

        List<Review> allReviews = reviewRepository.findAll();

        // Filter by productId
        if (productId != null) {
            allReviews = allReviews.stream()
                    .filter(r -> r.getProductId().equals(productId))
                    .collect(Collectors.toList());
        }

        // Filter by userId
        if (userId != null) {
            allReviews = allReviews.stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }

        // Filter by rating
        if (rating != null) {
            allReviews = allReviews.stream()
                    .filter(r -> r.getRating() == rating)
                    .collect(Collectors.toList());
        }

        // Filter by fromDate
        if (fromDate != null && !fromDate.isEmpty()) {
            LocalDateTime fromDateTime = LocalDateTime.of(LocalDate.parse(fromDate), LocalTime.MIN);
            allReviews = allReviews.stream()
                    .filter(r -> r.getCreatedAt() != null && !r.getCreatedAt().isBefore(fromDateTime))
                    .collect(Collectors.toList());
        }

        // Sort by createdAt desc
        allReviews.sort((a, b) -> {
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        // Manual pagination
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allReviews.size());

        if (start > allReviews.size()) {
            start = 0;
            end = 0;
        }

        List<Review> pageContent = allReviews.subList(start, end);
        List<AdminReviewResponseDTO> dtoList = pageContent.stream()
                .map(this::mapToAdminReviewDTO)
                .collect(Collectors.toList());

        Page<AdminReviewResponseDTO> result = new PageImpl<>(dtoList, pageable, allReviews.size());
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/admin/reviews/{id} - Delete a review (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        verifyAdminAccess();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        reviewRepository.delete(review);
        return ResponseEntity.noContent().build();
    }

    private AdminReviewResponseDTO mapToAdminReviewDTO(Review review) {
        AdminReviewResponseDTO dto = new AdminReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        // User info
        if (review.getUser() != null) {
            AdminReviewResponseDTO.AdminReviewUserDTO userDTO = new AdminReviewResponseDTO.AdminReviewUserDTO();
            userDTO.setId(review.getUser().getId());
            userDTO.setFullName(review.getUser().getFullName());
            userDTO.setAvatarUrl(review.getUser().getAvatarUrl());
            dto.setUser(userDTO);
        }

        // Product info
        if (review.getProduct() != null) {
            AdminReviewResponseDTO.AdminReviewProductDTO productDTO = new AdminReviewResponseDTO.AdminReviewProductDTO();
            productDTO.setId(review.getProduct().getId());
            productDTO.setName(review.getProduct().getName());
            dto.setProduct(productDTO);
        }

        return dto;
    }

    private void verifyAdminAccess() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPPORT) {
                throw new RuntimeException("Access denied: Admin role required");
            }
            return;
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type");
        }

        User user = userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPPORT) {
            throw new RuntimeException("Access denied: Admin role required");
        }
    }
}
