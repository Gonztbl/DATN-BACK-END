package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.ShipperProfile;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.ShipperProfileRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminControllerNew {

    private final UserRepository userRepository;
    private final ShipperProfileRepository shipperProfileRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public AdminControllerNew(
            UserRepository userRepository,
            ShipperProfileRepository shipperProfileRepository,
            RestaurantRepository restaurantRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.shipperProfileRepository = shipperProfileRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    // ==================== SHIPPER MANAGEMENT ====================

    @GetMapping("/shippers")
    public ResponseEntity<ShippersResponse> getShippers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isOnline) {

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<ShipperProfile> shipperProfiles = shipperProfileRepository.findShippersWithFilters(
                search, isOnline, pageable);

        List<ShipperDTO> shippers = shipperProfiles.getContent().stream()
                .map(this::mapToShipperDTO)
                .collect(Collectors.toList());

        ShippersResponse response = new ShippersResponse();
        response.setData(shippers);
        response.setPagination(new PaginationDTO(
                shipperProfiles.getTotalElements(),
                shipperProfiles.getNumber() + 1,
                shipperProfiles.getSize(),
                shipperProfiles.getTotalPages()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shippers/{id}")
    public ResponseEntity<ShipperDetailDTO> getShipperDetail(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));

        if (!user.getRole().equals(Role.SHIPPER)) {
            throw new RuntimeException("User is not a shipper");
        }

        ShipperProfile profile = shipperProfileRepository.findByUserId(id)
                .orElse(null);

        Page<Order> ordersPage = orderRepository.findByShipperIdAndStatus(id, null, null);
        List<Order> orders = ordersPage.getContent();

        ShipperDetailDTO response = mapToShipperDetailDTO(user, profile, orders);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/shippers/{id}/lock")
    public ResponseEntity<MessageResponse> lockShipper(@PathVariable Integer id,
                                                       @RequestBody(required = false) LockRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));

        if (!user.getRole().equals(Role.SHIPPER)) {
            throw new RuntimeException("User is not a shipper");
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Shipper locked successfully"));
    }

    @PutMapping("/shippers/{id}/unlock")
    public ResponseEntity<MessageResponse> unlockShipper(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));

        if (!user.getRole().equals(Role.SHIPPER)) {
            throw new RuntimeException("User is not a shipper");
        }

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Shipper unlocked successfully"));
    }

    // ==================== RESTAURANT OWNER MANAGEMENT ====================

    @GetMapping("/restaurant-owners")
    public ResponseEntity<RestaurantOwnersResponse> getRestaurantOwners(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<User> owners = userRepository.findByRoleAndSearch(Role.RESTAURANT_OWNER, search, pageable);

        List<RestaurantOwnerDTO> ownerDTOs = owners.getContent().stream()
                .map(this::mapToRestaurantOwnerDTO)
                .collect(Collectors.toList());

        RestaurantOwnersResponse response = new RestaurantOwnersResponse();
        response.setData(ownerDTOs);
        response.setPagination(new PaginationDTO(
                owners.getTotalElements(),
                owners.getNumber() + 1,
                owners.getSize(),
                owners.getTotalPages()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant-owners/{id}")
    public ResponseEntity<RestaurantOwnerDetailDTO> getRestaurantOwnerDetail(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));

        if (!user.getRole().equals(Role.RESTAURANT_OWNER)) {
            throw new RuntimeException("User is not a restaurant owner");
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(id);
        RestaurantOwnerDetailDTO response = mapToRestaurantOwnerDetailDTO(user, restaurants);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/restaurant-owners/{id}/lock")
    public ResponseEntity<MessageResponse> lockRestaurantOwner(@PathVariable Integer id,
                                                                @RequestBody(required = false) LockRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));

        if (!user.getRole().equals(Role.RESTAURANT_OWNER)) {
            throw new RuntimeException("User is not a restaurant owner");
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Restaurant owner locked successfully"));
    }

    @PutMapping("/restaurant-owners/{id}/unlock")
    public ResponseEntity<MessageResponse> unlockRestaurantOwner(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));

        if (!user.getRole().equals(Role.RESTAURANT_OWNER)) {
            throw new RuntimeException("User is not a restaurant owner");
        }

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Restaurant owner unlocked successfully"));
    }

    // ==================== SUPPORT TICKETS ====================
    // TODO: Implement SupportTicket entity and related APIs

    // ==================== MAPPING METHODS ====================

    private ShipperDTO mapToShipperDTO(ShipperProfile profile) {
        ShipperDTO dto = new ShipperDTO();
        dto.setUserId(profile.getUserId());
        dto.setFullName(profile.getUser().getFullName());
        dto.setPhone(profile.getUser().getPhone());
        dto.setEmail(profile.getUser().getEmail());
        dto.setVehicleType(profile.getVehicleType());
        dto.setVehiclePlate(profile.getVehiclePlate());
        dto.setIsOnline(profile.getIsOnline());
        dto.setIsActive(profile.getUser().isActive());
        dto.setCreatedAt(profile.getCreatedAt());
        return dto;
    }

    private ShipperDetailDTO mapToShipperDetailDTO(User user, ShipperProfile profile, List<Order> orders) {
        ShipperDetailDTO dto = new ShipperDetailDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());

        if (profile != null) {
            dto.setVehicleType(profile.getVehicleType());
            dto.setVehiclePlate(profile.getVehiclePlate());
            dto.setIsOnline(profile.getIsOnline());
            dto.setCurrentLat(profile.getCurrentLat());
            dto.setCurrentLng(profile.getCurrentLng());
        }

        dto.setTotalOrders(Long.valueOf(orders.size()));
        dto.setCompletedOrders(orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .count());

        return dto;
    }

    private RestaurantOwnerDTO mapToRestaurantOwnerDTO(User user) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private RestaurantOwnerDetailDTO mapToRestaurantOwnerDetailDTO(User user, List<Restaurant> restaurants) {
        RestaurantOwnerDetailDTO dto = new RestaurantOwnerDetailDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRestaurants(restaurants.stream()
                .map(this::mapToRestaurantDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private RestaurantDTO mapToRestaurantDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        dto.setAddress(restaurant.getAddress());
        dto.setStatus(restaurant.getStatus());
        dto.setProductCount(restaurant.getProductCount());
        dto.setCreatedAt(restaurant.getCreatedAt());
        return dto;
    }

    // ==================== DTO CLASSES ====================

    @Data
    public static class ShippersResponse {
        private List<ShipperDTO> data;
        private PaginationDTO pagination;
    }

    @Data
    public static class ShipperDTO {
        private Integer userId;
        private String fullName;
        private String phone;
        private String email;
        private String vehicleType;
        private String vehiclePlate;
        private Boolean isOnline;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data
    public static class ShipperDetailDTO {
        private Integer userId;
        private String fullName;
        private String phone;
        private String email;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private String vehicleType;
        private String vehiclePlate;
        private Boolean isOnline;
        private java.math.BigDecimal currentLat;
        private java.math.BigDecimal currentLng;
        private Long totalOrders;
        private Long completedOrders;
    }

    @Data
    public static class RestaurantOwnersResponse {
        private List<RestaurantOwnerDTO> data;
        private PaginationDTO pagination;
    }

    @Data
    public static class RestaurantOwnerDTO {
        private Integer userId;
        private String fullName;
        private String phone;
        private String email;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data
    public static class RestaurantOwnerDetailDTO {
        private Integer userId;
        private String fullName;
        private String phone;
        private String email;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private List<RestaurantDTO> restaurants;
    }

    @Data
    public static class RestaurantDTO {
        private String id;
        private String name;
        private String phone;
        private String email;
        private String address;
        private Boolean status;
        private Integer productCount;
        private LocalDateTime createdAt;
    }

    @Data
    public static class LockRequest {
        private String reason;
    }

    @Data
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }

    @Data
    public static class PaginationDTO {
        private Long total;
        private Integer page;
        private Integer limit;
        private Integer totalPages;

        public PaginationDTO(Long total, Integer page, Integer limit, Integer totalPages) {
            this.total = total;
            this.page = page;
            this.limit = limit;
            this.totalPages = totalPages;
        }
    }
}
