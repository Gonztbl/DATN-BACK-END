package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByShipperId(Integer shipperId);
    long countByShipperIdAndStatus(Integer shipperId, Order.OrderStatus status);
    long countByShipperIdAndCreatedAtBetween(Integer shipperId, LocalDateTime startDate, LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal sumTotalAmount();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal sumTotalAmountBetween(@org.springframework.data.repository.query.Param("startDate") LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") LocalDateTime endDate);

    long countByRestaurantId(String restaurantId);
    long countByRestaurantIdAndCreatedAtBetween(String restaurantId, LocalDateTime startDate, LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurantId = :restaurantId")
    BigDecimal sumTotalAmountByRestaurantId(@org.springframework.data.repository.query.Param("restaurantId") String restaurantId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal sumTotalAmountByRestaurantIdAndCreatedAtBetween(@org.springframework.data.repository.query.Param("restaurantId") String restaurantId, @org.springframework.data.repository.query.Param("startDate") LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") LocalDateTime endDate);
}
