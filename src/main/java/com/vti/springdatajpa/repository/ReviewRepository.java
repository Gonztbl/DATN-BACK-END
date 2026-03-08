package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    List<Review> findByProductId(Integer productId);
    
    Page<Review> findByProductId(Integer productId, Pageable pageable);
    
    List<Review> findByUserId(Integer userId);
}
