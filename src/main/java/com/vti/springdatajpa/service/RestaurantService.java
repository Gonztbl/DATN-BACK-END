package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RestaurantPageResponseDTO getRestaurants(int page, int limit, String search, Boolean status, String sortBy, String sortDir) {
        // Convert 1-based page to 0-based for Spring Data
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        
        // Validate sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Create pageable with sort
        Pageable pageable = PageRequest.of(zeroBasedPage, limit, Sort.by(direction, sortBy));
        
        // Get restaurants with filters
        Page<Restaurant> restaurantPage;
        if (search != null && !search.trim().isEmpty() || status != null) {
            restaurantPage = restaurantRepository.findRestaurantsWithFilters(
                search != null ? search.trim() : null, 
                status, 
                pageable
            );
        } else {
            restaurantPage = restaurantRepository.findByDeletedAtIsNull(pageable);
        }
        
        // Convert to DTOs
        List<RestaurantDetailDTO> restaurantDTOs = restaurantPage.getContent().stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantDetailDTO.class))
                .collect(Collectors.toList());
        
        // Build response
        RestaurantPageResponseDTO response = new RestaurantPageResponseDTO();
        response.setData(restaurantDTOs);
        
        RestaurantPageResponseDTO.PaginationInfo pagination = new RestaurantPageResponseDTO.PaginationInfo();
        pagination.setTotal(restaurantPage.getTotalElements());
        pagination.setPage(page);
        pagination.setLimit(limit);
        pagination.setTotalPages(restaurantPage.getTotalPages());
        response.setPagination(pagination);
        
        return response;
    }

    public RestaurantDetailDTO getRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        return modelMapper.map(restaurant, RestaurantDetailDTO.class);
    }

    public RestaurantDetailDTO createRestaurant(RestaurantCreateRequestDTO request) {
        // Find user by userId
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
                
        // Verify user has RESTAURANT_OWNER role
        if (user.getRole() != Role.RESTAURANT_OWNER) {
            throw new RuntimeException("User does not have RESTAURANT_OWNER role");
        }
        
        // Check if name already exists
        if (restaurantRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Restaurant name already exists: " + request.getName());
        }
        
        Restaurant restaurant = modelMapper.map(request, Restaurant.class);
        restaurant.setOwnerId(user.getId());
        restaurant.setProductCount(0);
        restaurant.setDeletedAt(null);
        restaurant.setDescription(request.getDescription());
        restaurant.setCategoryId(request.getCategoryId());
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        return modelMapper.map(savedRestaurant, RestaurantDetailDTO.class);
    }

    public RestaurantDetailDTO updateRestaurant(String id, RestaurantUpdateRequestDTO request) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        // Check if name already exists (excluding current restaurant)
        if (request.getName() != null && !request.getName().equals(restaurant.getName())) {
            if (restaurantRepository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
                throw new RuntimeException("Restaurant name already exists: " + request.getName());
            }
            restaurant.setName(request.getName());
        }
        
        // Update other fields if provided
        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getLogoBase64() != null) {
            restaurant.setLogoBase64(request.getLogoBase64());
        }
        if (request.getStatus() != null) {
            restaurant.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            restaurant.setCategoryId(request.getCategoryId());
        }
        
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        
        return modelMapper.map(updatedRestaurant, RestaurantDetailDTO.class);
    }

    public void deleteRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        // Soft delete all associated products
        productRepository.softDeleteByRestaurantId(id);
        
        // Soft delete
        restaurant.setDeletedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);
    }

    public RestaurantCheckNameDTO checkRestaurantName(String name, String excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = restaurantRepository.existsByNameAndIdNotAndDeletedAtIsNull(name, excludeId);
        } else {
            exists = restaurantRepository.existsByNameAndDeletedAtIsNull(name);
        }
        
        RestaurantCheckNameDTO response = new RestaurantCheckNameDTO();
        response.setExists(exists);
        
        return response;
    }

    public byte[] exportRestaurants(String search, Boolean status) throws IOException {
        // Get all restaurants for export (without pagination)
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "name"));
        Page<Restaurant> restaurantPage;
        
        if (search != null && !search.trim().isEmpty() || status != null) {
            restaurantPage = restaurantRepository.findRestaurantsWithFilters(
                search != null ? search.trim() : null, 
                status, 
                pageable
            );
        } else {
            restaurantPage = restaurantRepository.findByDeletedAtIsNull(pageable);
        }
        
        // Create CSV content
        StringBuilder csvContent = new StringBuilder();
        
        // Header
        csvContent.append("ID,Tên,SĐT,Email,Địa chỉ,Trạng thái,Số món,Ngày tạo\n");
        
        // Data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Restaurant restaurant : restaurantPage.getContent()) {
            csvContent.append("\"").append(restaurant.getId()).append("\",");
            csvContent.append("\"").append(restaurant.getName()).append("\",");
            csvContent.append("\"").append(restaurant.getPhone() != null ? restaurant.getPhone() : "").append("\",");
            csvContent.append("\"").append(restaurant.getEmail() != null ? restaurant.getEmail() : "").append("\",");
            csvContent.append("\"").append(restaurant.getAddress()).append("\",");
            csvContent.append("\"").append(restaurant.getStatus() ? "Mở" : "Đóng").append("\",");
            csvContent.append(restaurant.getProductCount()).append(",");
            csvContent.append("\"").append(restaurant.getCreatedAt().format(formatter)).append("\"");
            csvContent.append("\n");
        }
        
        return csvContent.toString().getBytes("UTF-8");
    }
}
