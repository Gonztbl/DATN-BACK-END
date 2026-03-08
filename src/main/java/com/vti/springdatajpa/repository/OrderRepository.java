package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    List<Order> findByUserId(Integer userId);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
