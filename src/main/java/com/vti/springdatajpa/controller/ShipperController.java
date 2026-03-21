package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    public ResponseEntity<ShipperOrdersResponse> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean assigned,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Integer shipperId = getCurrentUserId();
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        Page<Order> ordersPage;

        if (assigned != null) {
            if (assigned) {
                // Get orders assigned to this shipper
                if (status != null && !status.isEmpty()) {
                    try {
                        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                        ordersPage = orderRepository.findByShipperIdAndStatus(shipperId, orderStatus, pageable);
                    } catch (IllegalArgumentException e) {
                        ordersPage = orderRepository.findByShipperId(shipperId, pageable);
                    }
                } else {
                    ordersPage = orderRepository.findByShipperId(shipperId, pageable);
                }
            } else {
                // Get available orders (READY_FOR_PICKUP with no shipper)
                ordersPage = orderRepository.findByStatusAndShipperIdIsNull(Order.OrderStatus.READY_FOR_PICKUP, pageable);
            }
        } else {
            // Default: get assigned orders
            if (status != null && !status.isEmpty()) {
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    ordersPage = orderRepository.findByShipperIdAndStatus(shipperId, orderStatus, pageable);
                } catch (IllegalArgumentException e) {
                    ordersPage = orderRepository.findByShipperId(shipperId, pageable);
                }
            } else {
                ordersPage = orderRepository.findByShipperId(shipperId, pageable);
            }
        }

        List<ShipperOrderDTO> orders = ordersPage.getContent().stream()
                .map(this::mapToShipperOrderDTO)
                .collect(Collectors.toList());

        ShipperOrdersResponse response = new ShipperOrdersResponse();
        response.setData(orders);
        response.setPagination(new PaginationDTO(
                ordersPage.getTotalElements(),
                page,
                limit,
                ordersPage.getTotalPages()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ShipperOrderDetailDTO> getOrderDetail(@PathVariable Integer id) {
        Integer shipperId = getCurrentUserId();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if order is assigned to this shipper or is available
        if (order.getShipperId() != null && !order.getShipperId().equals(shipperId)) {
            throw new RuntimeException("Access denied: Order assigned to another shipper");
        }

        if (order.getShipperId() == null && order.getStatus() != Order.OrderStatus.READY_FOR_PICKUP) {
            throw new RuntimeException("Order is not available for pickup");
        }

        return ResponseEntity.ok(mapToShipperOrderDetailDTO(order));
    }

    @PutMapping("/orders/{id}/accept")
    @Transactional
    public ResponseEntity<OrderAcceptResponse> acceptOrder(@PathVariable Integer id) {
        Integer shipperId = getCurrentUserId();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if order is available
        if (order.getStatus() != Order.OrderStatus.READY_FOR_PICKUP) {
            throw new RuntimeException("Order is not available for pickup");
        }

        if (order.getShipperId() != null) {
            throw new RuntimeException("Order already assigned to another shipper");
        }

        // Assign order to shipper
        order.setShipperId(shipperId);
        orderRepository.save(order);

        OrderAcceptResponse response = new OrderAcceptResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Order accepted");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/picked-up")
    public ResponseEntity<OrderStatusResponse> markPickedUp(@PathVariable Integer id) {
        Integer shipperId = getCurrentUserId();

        Order order = orderRepository.findByIdAndShipperId(id, shipperId)
                .orElseThrow(() -> new RuntimeException("Order not found or not assigned to you"));

        if (order.getStatus() != Order.OrderStatus.READY_FOR_PICKUP) {
            throw new RuntimeException("Order must be READY_FOR_PICKUP to mark as picked up");
        }

        order.setStatus(Order.OrderStatus.DELIVERING);
        order.setPickedUpAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Picked up, on the way");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/delivered")
    public ResponseEntity<OrderStatusResponse> markDelivered(
            @PathVariable Integer id,
            @RequestBody(required = false) DeliveredRequest request) {

        Integer shipperId = getCurrentUserId();

        Order order = orderRepository.findByIdAndShipperId(id, shipperId)
                .orElseThrow(() -> new RuntimeException("Order not found or not assigned to you"));

        if (order.getStatus() != Order.OrderStatus.DELIVERING) {
            throw new RuntimeException("Order must be DELIVERING to mark as delivered");
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setDeliveredAt(LocalDateTime.now());
        // Save photo if provided
        // TODO: Handle photo upload
        orderRepository.save(order);

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Delivered successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/failed")
    public ResponseEntity<OrderStatusResponse> markDeliveryFailed(
            @PathVariable Integer id,
            @Valid @RequestBody DeliveryFailedRequest request) {

        Integer shipperId = getCurrentUserId();

        Order order = orderRepository.findByIdAndShipperId(id, shipperId)
                .orElseThrow(() -> new RuntimeException("Order not found or not assigned to you"));

        if (order.getStatus() != Order.OrderStatus.DELIVERING) {
            throw new RuntimeException("Order must be DELIVERING to mark as failed");
        }

        order.setStatus(Order.OrderStatus.DELIVERY_FAILED);
        order.setDeliveryFailedReason(request.getReason());
        // Save photo if provided
        // TODO: Handle photo upload
        orderRepository.save(order);

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Delivery failed, waiting for further instructions");

        return ResponseEntity.ok(response);
    }

    // ==================== HELPER METHODS ====================

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }

        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ShipperOrderDTO mapToShipperOrderDTO(Order order) {
        ShipperOrderDTO dto = new ShipperOrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode("#" + order.getId());

        // Get restaurant info
        if (order.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId()).orElse(null);
            if (restaurant != null) {
                dto.setRestaurantName(restaurant.getName());
                dto.setRestaurantAddress(restaurant.getAddress());
            }
        }

        // Get customer info
        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerPhone(order.getUser().getPhone());
        }

        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setReadyAt(order.getReadyAt());
        dto.setPickedUpAt(order.getPickedUpAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        return dto;
    }

    private ShipperOrderDetailDTO mapToShipperOrderDetailDTO(Order order) {
        ShipperOrderDetailDTO dto = new ShipperOrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderCode("#" + order.getId());

        // Restaurant info
        if (order.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId()).orElse(null);
            if (restaurant != null) {
                RestaurantDTO restDTO = new RestaurantDTO();
                restDTO.setName(restaurant.getName());
                restDTO.setAddress(restaurant.getAddress());
                restDTO.setPhone(restaurant.getPhone());
                // Mock coordinates - should be from actual data
                restDTO.setLatitude(21.0285);
                restDTO.setLongitude(105.8542);
                dto.setRestaurant(restDTO);
            }
        }

        // Customer info
        CustomerDTO custDTO = new CustomerDTO();
        if (order.getUser() != null) {
            custDTO.setName(order.getUser().getFullName());
            custDTO.setPhone(order.getUser().getPhone());
        }
        custDTO.setAddress(order.getDeliveryAddress());
        // Mock coordinates
        custDTO.setLatitude(21.0285);
        custDTO.setLongitude(105.8542);
        dto.setCustomer(custDTO);

        // Items
        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                    .map(item -> new ShipperOrderItemDTO(
                            item.getProduct() != null ? item.getProduct().getName() : null,
                            item.getQuantity(),
                            item.getNote()
                    ))
                    .collect(Collectors.toList()));
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus("PAID"); // Mock - should check actual transaction
        dto.setNote(order.getNote());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setReadyAt(order.getReadyAt());
        dto.setPickedUpAt(order.getPickedUpAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        return dto;
    }

    // ==================== DTO CLASSES ====================

    @Data
    public static class ShipperOrdersResponse {
        private List<ShipperOrderDTO> data;
        private PaginationDTO pagination;
    }

    @Data
    public static class ShipperOrderDTO {
        private Integer id;
        private String orderCode;
        private String restaurantName;
        private String restaurantAddress;
        private String customerName;
        private String customerPhone;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private LocalDateTime createdAt;
        private LocalDateTime readyAt;
        private LocalDateTime pickedUpAt;
        private LocalDateTime deliveredAt;
    }

    @Data
    public static class ShipperOrderDetailDTO {
        private Integer id;
        private String orderCode;
        private RestaurantDTO restaurant;
        private CustomerDTO customer;
        private List<ShipperOrderItemDTO> items;
        private BigDecimal totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private String note;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime readyAt;
        private LocalDateTime pickedUpAt;
        private LocalDateTime deliveredAt;
    }

    @Data
    public static class RestaurantDTO {
        private String name;
        private String address;
        private String phone;
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class CustomerDTO {
        private String name;
        private String phone;
        private String address;
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class ShipperOrderItemDTO {
        private String productName;
        private Integer quantity;
        private String note;

        public ShipperOrderItemDTO(String productName, Integer quantity, String note) {
            this.productName = productName;
            this.quantity = quantity;
            this.note = note;
        }
    }

    @Data
    public static class OrderAcceptResponse {
        private Integer id;
        private String status;
        private String message;
    }

    @Data
    public static class OrderStatusResponse {
        private Integer id;
        private String status;
        private String message;
    }

    @Data
    public static class DeliveredRequest {
        private String photoBase64;
    }

    @Data
    public static class DeliveryFailedRequest {
        private String reason;
        private String photoBase64;
    }

    @Data
    public static class PaginationDTO {
        private long total;
        private int page;
        private int limit;
        private int totalPages;

        public PaginationDTO(long total, int page, int limit, int totalPages) {
            this.total = total;
            this.page = page;
            this.limit = limit;
            this.totalPages = totalPages;
        }
    }
}
