package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.OrderResponseDTO;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.service.OrderService;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    /**
     * GET /api/orders - Get list of orders for current user
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Integer userId = getCurrentUserId();

        // Parse sort parameter (e.g., "createdAt,desc")
        Sort.Direction direction = Sort.Direction.DESC;
        String sortField = "createdAt";

        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sortField = parts[0];
            direction = parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Parse status if provided
        Order.OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, will return all orders
            }
        }

        Page<OrderResponseDTO> orders = orderService.getOrdersForUser(userId, orderStatus, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id} - Get order detail by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderDetail(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        OrderResponseDTO order = orderService.getOrderDetail(id, userId);
        return ResponseEntity.ok(order);
    }

    /**
     * PUT /api/orders/{id}/cancel - Cancel order
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderService.CancelOrderResponse> cancelOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) CancelOrderRequest request) {
        Integer userId = getCurrentUserId();
        String reason = request != null ? request.getReason() : null;
        OrderService.CancelOrderResponse response = orderService.cancelOrder(id, userId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/orders/{id}/reorder - Reorder from existing order
     */
    @PostMapping("/{id}/reorder")
    public ResponseEntity<OrderService.ReorderResponse> reorder(
            @PathVariable Integer id,
            @RequestBody(required = false) ReorderRequest request) {
        Integer userId = getCurrentUserId();
        OrderService.ReorderRequest serviceRequest = new OrderService.ReorderRequest();
        if (request != null) {
            serviceRequest.setDeliveryAddress(request.getDeliveryAddress());
            serviceRequest.setNote(request.getNote());
        }
        OrderService.ReorderResponse response = orderService.reorder(id, userId, serviceRequest);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * GET /api/orders/tracking/{id} - Track order status
     */
    @GetMapping("/tracking/{id}")
    public ResponseEntity<OrderService.OrderTrackingResponse> trackOrder(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        OrderService.OrderTrackingResponse response = orderService.trackOrder(id, userId);
        return ResponseEntity.ok(response);
    }

    @lombok.Data
    public static class CancelOrderRequest {
        private String reason;
    }

    @lombok.Data
    public static class ReorderRequest {
        private String deliveryAddress;
        private String note;
    }

    /**
     * Get current user ID from JWT token
     */
    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }

        // Find user by username or email
        return userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
