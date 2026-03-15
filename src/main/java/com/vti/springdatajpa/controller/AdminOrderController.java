package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.service.AdminOrderService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final UserRepository userRepository;

    /**
     * GET /api/admin/orders - Get all orders (admin only)
     */
    @GetMapping
    public ResponseEntity<Page<AdminOrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        verifyAdminAccess();

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Parse dates
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        if (fromDate != null && !fromDate.isEmpty()) {
            fromDateTime = LocalDateTime.of(LocalDate.parse(fromDate), LocalTime.MIN);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toDateTime = LocalDateTime.of(LocalDate.parse(toDate), LocalTime.MAX);
        }

        // Parse status
        Order.OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<AdminOrderResponseDTO> orders = adminOrderService.getAllOrders(
                orderStatus, userId, restaurantId, fromDateTime, toDateTime, search, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/admin/orders/{id} - Get order detail (admin only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDetailDTO> getOrderDetail(@PathVariable Integer id) {
        verifyAdminAccess();
        AdminOrderDetailDTO order = adminOrderService.getOrderDetail(id);
        return ResponseEntity.ok(order);
    }

    /**
     * PUT /api/admin/orders/{id}/status - Update order status (admin only)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AdminOrderService.AdminOrderUpdateResponseDTO> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderStatusRequest request) {
        verifyAdminAccess();
        AdminOrderService.AdminOrderUpdateResponseDTO response = adminOrderService.updateOrderStatus(id, request.getStatus(), request.getNote());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/admin/orders/{id}/cancel - Cancel order (admin only)
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AdminOrderService.AdminOrderCancelResponseDTO> cancelOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) CancelOrderRequest request) {
        verifyAdminAccess();
        String reason = request != null ? request.getReason() : null;
        AdminOrderService.AdminOrderCancelResponseDTO response = adminOrderService.cancelOrder(id, reason);
        return ResponseEntity.ok(response);
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

    @Data
    public static class UpdateOrderStatusRequest {
        private String status;
        private String note;
    }

    @Data
    public static class CancelOrderRequest {
        private String reason;
    }

    @Data
    public static class AdminOrderUpdateResponseDTO {
        private Integer id;
        private String status;
        private LocalDateTime updatedAt;
        private String message;
    }

    @Data
    public static class AdminOrderCancelResponseDTO {
        private Integer id;
        private String status;
        private Integer refundTransactionId;
        private String message;
    }
}
