package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<Order> findByUserId(Integer userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Integer userId, Order.OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUserId(Integer id, Integer userId);

    // Restaurant Owner methods
    Page<Order> findByRestaurantId(String restaurantId, Pageable pageable);

    Page<Order> findByRestaurantIdAndStatus(String restaurantId, Order.OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndRestaurantId(Integer id, String restaurantId);

    // Shipper methods
    Page<Order> findByShipperId(Integer shipperId, Pageable pageable);

    Page<Order> findByShipperIdAndStatus(Integer shipperId, Order.OrderStatus status, Pageable pageable);

    List<Order> findByStatusAndShipperIdIsNull(Order.OrderStatus status);

    Optional<Order> findByIdAndShipperId(Integer id, Integer shipperId);

    void deleteByUserId(Integer userId);
    void deleteByShipperId(Integer shipperId);
}
