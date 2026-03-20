package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.ReviewResponseDTO;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.entity.Review;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<ReviewResponseDTO> getReviewsByProductId(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review -> mapToDTO(review, product))
                .collect(Collectors.toList());
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
