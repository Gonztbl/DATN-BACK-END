package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    List<OrderItem> findByOrderId(Integer orderId);

    @Modifying
    @Query(value = "DELETE oi FROM order_items oi INNER JOIN orders o ON oi.order_id = o.id WHERE o.user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Integer userId);
}
