package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurant-owner")
public class RestaurantOwnerController {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public RestaurantOwnerController(
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    public ResponseEntity<OrdersResponse> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        Page<Order> ordersPage;
        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                ordersPage = orderRepository.findByRestaurantIdAndStatus(restaurant.getId(), orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                ordersPage = orderRepository.findByRestaurantId(restaurant.getId(), pageable);
            }
        } else {
            ordersPage = orderRepository.findByRestaurantId(restaurant.getId(), pageable);
        }

        List<OrderDTO> orders = ordersPage.getContent().stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());

        OrdersResponse response = new OrdersResponse();
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
    public ResponseEntity<OrderDetailDTO> getOrderDetail(@PathVariable Integer id) {
        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Order order = orderRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return ResponseEntity.ok(mapToOrderDetailDTO(order));
    }

    @PutMapping("/orders/{id}/confirm")
    public ResponseEntity<OrderStatusResponse> confirmOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) ConfirmOrderRequest request) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Order order = orderRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order can only be confirmed when in PENDING status");
        }

        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Order confirmed successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/reject")
    public ResponseEntity<OrderStatusResponse> rejectOrder(
            @PathVariable Integer id,
            @Valid @RequestBody RejectOrderRequest request) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Order order = orderRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order cannot be rejected at this status");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setRejectedReason(request.getReason());
        orderRepository.save(order);

        // TODO: Process refund if order was paid online

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Order rejected");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/ready")
    public ResponseEntity<OrderStatusResponse> markOrderReady(@PathVariable Integer id) {
        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Order order = orderRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED && order.getStatus() != Order.OrderStatus.PREPARING) {
            throw new RuntimeException("Order must be CONFIRMED or PREPARING to mark as ready");
        }

        order.setStatus(Order.OrderStatus.READY_FOR_PICKUP);
        order.setReadyAt(LocalDateTime.now());
        orderRepository.save(order);

        OrderStatusResponse response = new OrderStatusResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setMessage("Order is ready for pickup");

        return ResponseEntity.ok(response);
    }

    // ==================== PRODUCT MANAGEMENT ====================

    @GetMapping("/products")
    public ResponseEntity<ProductsResponse> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        Page<Product> productsPage;
        if (status != null && !status.isEmpty()) {
            productsPage = productRepository.findByRestaurantIdAndStatusAndDeletedAtIsNull(
                    restaurant.getId(), status, pageable);
        } else {
            productsPage = productRepository.findByRestaurantIdAndDeletedAtIsNull(restaurant.getId(), pageable);
        }

        List<ProductDTO> products = productsPage.getContent().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());

        ProductsResponse response = new ProductsResponse();
        response.setData(products);
        response.setPagination(new PaginationDTO(
                productsPage.getTotalElements(),
                page,
                limit,
                productsPage.getTotalPages()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductDetail(@PathVariable Integer id) {
        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ResponseEntity.ok(mapToProductDTO(product));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageBase64(request.getImageBase64());
        product.setCategoryId(request.getCategoryId());
        product.setRestaurantId(restaurant.getId());
        product.setStatus(request.getStatus() != null ? request.getStatus() : "available");

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(mapToProductDTO(saved));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getImageBase64() != null) {
            product.setImageBase64(request.getImageBase64());
        }
        product.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(mapToProductDTO(saved));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }

    // ==================== RESTAURANT STATUS ====================

    @PutMapping("/restaurant/status")
    public ResponseEntity<RestaurantStatusResponse> updateRestaurantStatus(
            @Valid @RequestBody UpdateRestaurantStatusRequest request) {

        Integer ownerId = getCurrentUserId();
        Restaurant restaurant = getRestaurantByOwner(ownerId);

        restaurant.setStatus(request.getIsOpen());
        restaurantRepository.save(restaurant);

        RestaurantStatusResponse response = new RestaurantStatusResponse();
        response.setRestaurantId(restaurant.getId());
        response.setIsOpen(request.getIsOpen());
        response.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ==================== HELPER METHODS ====================

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private Restaurant getRestaurantByOwner(Integer ownerId) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        if (restaurants.isEmpty()) {
            throw new RuntimeException("Restaurant not found for this owner");
        }
        return restaurants.get(0);
    }

    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode("#" + order.getId());
        dto.setUserId(order.getUserId());

        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerPhone(order.getUser().getPhone());
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                    .map(item -> new OrderItemDTO(
                            item.getProductId(),
                            item.getProduct() != null ? item.getProduct().getName() : null,
                            item.getQuantity(),
                            item.getPriceAtTime()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private OrderDetailDTO mapToOrderDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderCode("#" + order.getId());
        dto.setUserId(order.getUserId());

        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerPhone(order.getUser().getPhone());
        }

        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setNote(order.getNote());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());

        // Mock payment status - should check actual transaction
        dto.setPaymentStatus("PAID");
        dto.setTransactionId("TXN-" + order.getId());

        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                    .map(item -> new OrderItemDetailDTO(
                            item.getProductId(),
                            item.getProduct() != null ? item.getProduct().getName() : null,
                            item.getQuantity(),
                            item.getPriceAtTime(),
                            item.getNote()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageBase64(product.getImageBase64());
        dto.setCategoryId(product.getCategoryId());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }

    // ==================== DTO CLASSES ====================

    @Data
    public static class OrdersResponse {
        private List<OrderDTO> data;
        private PaginationDTO pagination;
    }

    @Data
    public static class OrderDTO {
        private Integer id;
        private String orderCode;
        private Integer userId;
        private String customerName;
        private String customerPhone;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private LocalDateTime createdAt;
        private List<OrderItemDTO> items;
    }

    @Data
    public static class OrderItemDTO {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;

        public OrderItemDTO(Integer productId, String productName, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
    }

    @Data
    public static class OrderDetailDTO {
        private Integer id;
        private String orderCode;
        private Integer userId;
        private String customerName;
        private String customerPhone;
        private String deliveryAddress;
        private String note;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private String paymentStatus;
        private String transactionId;
        private LocalDateTime createdAt;
        private List<OrderItemDetailDTO> items;
    }

    @Data
    public static class OrderItemDetailDTO {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private String note;

        public OrderItemDetailDTO(Integer productId, String productName, Integer quantity, BigDecimal price, String note) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.note = note;
        }
    }

    @Data
    public static class OrderStatusResponse {
        private Integer id;
        private String status;
        private String message;
    }

    @Data
    public static class ConfirmOrderRequest {
        private LocalDateTime estimatedReadyTime;
    }

    @Data
    public static class RejectOrderRequest {
        private String reason;
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

    @Data
    public static class ProductsResponse {
        private List<ProductDTO> data;
        private PaginationDTO pagination;
    }

    @Data
    public static class ProductDTO {
        private Integer id;
        private String name;
        private String description;
        private BigDecimal price;
        private String imageBase64;
        private Integer categoryId;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class CreateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String imageBase64;
        private Integer categoryId;
        private String status;
    }

    @Data
    public static class UpdateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String imageBase64;
        private Integer categoryId;
        private String status;
    }

    @Data
    public static class UpdateRestaurantStatusRequest {
        private Boolean isOpen;
    }

    @Data
    public static class RestaurantStatusResponse {
        private String restaurantId;
        private Boolean isOpen;
        private LocalDateTime updatedAt;
    }
}
