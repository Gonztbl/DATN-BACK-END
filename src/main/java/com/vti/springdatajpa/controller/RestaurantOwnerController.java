package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.WalletService;
import com.vti.springdatajpa.dto.WalletBalanceDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final WalletService walletService;
    private static final com.fasterxml.jackson.databind.ObjectMapper OBJECT_MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();

    public RestaurantOwnerController(
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            WalletService walletService) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    // ==================== RESTAURANT MANAGEMENT ====================

    @GetMapping("/my-restaurant")
    public ResponseEntity<?> getMyRestaurant() {
        User user = getCurrentUser();

        // Check if role is RESTAURANT_OWNER
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        
        // Status 404 if no restaurant exists
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }

        Restaurant restaurant = restaurants.get(0);
        return ResponseEntity.ok(mapToMyRestaurantDTO(restaurant));
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(
            @RequestParam(required = false, defaultValue = "today") String period,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                start = LocalDateTime.parse(fromDate);
                if (toDate != null && !toDate.isEmpty()) {
                    end = LocalDateTime.parse(toDate);
                }
            } else {
                switch (period.toLowerCase()) {
                    case "week":
                        start = end.minusWeeks(1);
                        break;
                    case "month":
                        start = end.minusMonths(1);
                        break;
                    case "today":
                    default:
                        start = end.toLocalDate().atStartOfDay();
                        break;
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    400, "Bad Request", "Invalid date format or period", e.getMessage(), LocalDateTime.now().toString()
            ));
        }

        BigDecimal revenue = orderRepository.sumTotalAmountByRestaurantIdAndCreatedAtBetween(
                restaurant.getId(), start, end);
        if (revenue == null) revenue = BigDecimal.ZERO;

        long orderCount = orderRepository.countByRestaurantIdAndCreatedAtBetween(
                restaurant.getId(), start, end);

        return ResponseEntity.ok(RevenueResponseDTO.builder()
                .revenue(revenue)
                .orderCount(orderCount)
                .period(period)
                .build());
    }

    @GetMapping("/wallet/balance")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance() {
        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            throw new RuntimeException("Forbidden: You do not have RESTAURANT_OWNER role");
        }
        
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(walletService.getBalance(identity));
    }

    private MyRestaurantResponseDTO mapToMyRestaurantDTO(Restaurant restaurant) {
        List<ScheduleDTO> parsedSchedule = null;
        if (restaurant.getSchedule() != null && !restaurant.getSchedule().isBlank()) {
            try {
                parsedSchedule = OBJECT_MAPPER.readValue(
                    restaurant.getSchedule(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<ScheduleDTO>>() {}
                );
            } catch (Exception e) {
                // ignore or log
            }
        }

        return MyRestaurantResponseDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .description(restaurant.getDescription())
                .logoBase64(restaurant.getLogoBase64())
                .status(restaurant.getStatus() ? "OPEN" : "CLOSED")
                .ownerId(restaurant.getOwnerId())
                .createdAt(restaurant.getCreatedAt())
                .categoryId(restaurant.getCategoryId())
                .schedule(parsedSchedule)
                .build();
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private Object details;
        private String timestamp;
    }

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

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
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

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
    public ResponseEntity<?> getProductDetail(@PathVariable Integer id) {
        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ResponseEntity.ok(mapToProductDTO(product));
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageBase64(request.getImageBase64());
        product.setCategoryId(request.getCategoryId());
        product.setRestaurantId(restaurant.getId());
        String status = request.getStatus();
        if (status == null || status.isBlank()) {
            status = "available";
        } else {
            status = status.toLowerCase();
        }
        product.setStatus(status);

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(mapToProductDTO(saved));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getImageBase64() != null) {
            product.setImageBase64(request.getImageBase64());
        }
        product.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            product.setStatus(request.getStatus().toLowerCase());
        } else if (product.getStatus() == null || product.getStatus().isBlank()) {
            product.setStatus("available");
        }

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(mapToProductDTO(saved));
    }

    @PutMapping("/products/{id}/status")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, String> body) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body("Status is required");
        }

        product.setStatus(status.toLowerCase());
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(mapToProductDTO(saved));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Product product = productRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }

    // ==================== RESTAURANT STATUS & INFO ====================

    @PutMapping("/restaurant/info")
    public ResponseEntity<?> updateRestaurantInfo(
            @Valid @RequestBody UpdateRestaurantInfoRequest request) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        restaurant.setName(request.getName());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            restaurant.setStatus("OPEN".equalsIgnoreCase(request.getStatus()));
        }

        if (request.getSchedule() != null) {
            try {
                String scheduleJson = OBJECT_MAPPER.writeValueAsString(request.getSchedule());
                restaurant.setSchedule(scheduleJson);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        400, "Bad Request", "Invalid schedule format", e.getMessage(), LocalDateTime.now().toString()
                ));
            }
        }

        restaurantRepository.save(restaurant);
        return ResponseEntity.ok(mapToMyRestaurantDTO(restaurant));
    }

    @PutMapping("/restaurant/status")
    public ResponseEntity<?> updateRestaurantStatus(
            @Valid @RequestBody UpdateRestaurantStatusRequest request) {

        User user = getCurrentUser();
        if (user.getRole() != com.vti.springdatajpa.entity.enums.Role.RESTAURANT_OWNER) {
            return roleForbiddenResponse();
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
        if (restaurants.isEmpty()) {
            return restaurantNotFoundResponse();
        }
        Restaurant restaurant = restaurants.get(0);

        Boolean isOpen = "OPEN".equalsIgnoreCase(request.getStatus());
        restaurant.setStatus(isOpen);
        restaurantRepository.save(restaurant);

        RestaurantStatusResponse response = new RestaurantStatusResponse();
        response.setRestaurantId(restaurant.getId());
        response.setIsOpen(isOpen);
        response.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ==================== HELPER METHODS ====================

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        }
        
        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ResponseEntity<ErrorResponse> roleForbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                403,
                "Forbidden",
                "You do not have RESTAURANT_OWNER role",
                null,
                LocalDateTime.now().toString()
        ));
    }

    private ResponseEntity<ErrorResponse> restaurantNotFoundResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                404,
                "Not Found",
                "You do not own any restaurant yet",
                null,
                LocalDateTime.now().toString()
        ));
    }

    private Integer getCurrentUserId() {
        return getCurrentUser().getId();
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
        @NotNull(message = "status field is required")
        private String status;
    }

    @Data
    public static class RestaurantStatusResponse {
        private String restaurantId;
        private Boolean isOpen;
        private LocalDateTime updatedAt;
    }
}
