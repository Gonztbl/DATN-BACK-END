package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.OwnerStatisticsDTO;
import com.vti.springdatajpa.dto.RestaurantOwnerDetailDTO;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRestaurantOwnerService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final UserManageService userManageService;

    private User getOwnerOrThrow(Integer ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        if (user.getRole() != Role.RESTAURANT_OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a restaurant owner");
        }
        return user;
    }

    public RestaurantOwnerDetailDTO getOwnerDetail(Integer id) {
        User user = getOwnerOrThrow(id);

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(id);

        List<RestaurantOwnerDetailDTO.RestaurantOverviewDTO> restaurantDTOs = restaurants.stream()
                .map(r -> new RestaurantOwnerDetailDTO.RestaurantOverviewDTO(
                        r.getId(),
                        r.getName(),
                        r.getStatus() != null && r.getStatus(),
                        r.getProductCount()
                ))
                .collect(Collectors.toList());

        return RestaurantOwnerDetailDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .restaurants(restaurantDTOs)
                .build();
    }

    public List<RestaurantOwnerDetailDTO.RestaurantOverviewDTO> getOwnerRestaurants(Integer id) {
        getOwnerOrThrow(id);
        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(id);

        return restaurants.stream()
                .map(r -> new RestaurantOwnerDetailDTO.RestaurantOverviewDTO(
                        r.getId(),
                        r.getName(),
                        r.getStatus() != null && r.getStatus(),
                        r.getProductCount()
                ))
                .collect(Collectors.toList());
    }

    public OwnerStatisticsDTO getOwnerStatistics(Integer id, LocalDate fromDate, LocalDate toDate) {
        getOwnerOrThrow(id);

        List<Restaurant> restaurants = restaurantRepository.findByOwnerIdAndDeletedAtIsNull(id);

        BigDecimal grandTotalRevenue = BigDecimal.ZERO;
        long grandTotalOrders = 0;

        List<OwnerStatisticsDTO.RestaurantStatDTO> restaurantStats = new ArrayList<>();

        LocalDateTime startDt = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime endDt = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        for (Restaurant r : restaurants) {
            long orders;
            BigDecimal revenue;

            if (startDt != null && endDt != null) {
                orders = orderRepository.countByRestaurantIdAndCreatedAtBetween(r.getId(), startDt, endDt);
                revenue = orderRepository.sumTotalAmountByRestaurantIdAndCreatedAtBetween(r.getId(), startDt, endDt);
            } else {
                orders = orderRepository.countByRestaurantId(r.getId());
                revenue = orderRepository.sumTotalAmountByRestaurantId(r.getId());
            }

            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }

            grandTotalOrders += orders;
            grandTotalRevenue = grandTotalRevenue.add(revenue);

            restaurantStats.add(new OwnerStatisticsDTO.RestaurantStatDTO(r.getId(), r.getName(), revenue, orders));
        }

        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (grandTotalOrders > 0) {
            avgOrderValue = grandTotalRevenue.divide(BigDecimal.valueOf(grandTotalOrders), 2, java.math.RoundingMode.HALF_UP);
        }

        return OwnerStatisticsDTO.builder()
                .totalRevenue(grandTotalRevenue)
                .totalOrders(grandTotalOrders)
                .avgOrderValue(avgOrderValue)
                .restaurants(restaurantStats)
                .build();
    }

    public org.springframework.data.domain.Page<RestaurantOwnerDetailDTO> getRestaurantOwners(String search, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<User> owners = userRepository.findByRoleAndSearch(Role.RESTAURANT_OWNER, search, pageable);
        return owners.map(user -> RestaurantOwnerDetailDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build());
    }

    public void lockRestaurantOwner(Integer id) {
        getOwnerOrThrow(id);
        userManageService.lockUser(id);
    }

    public void unlockRestaurantOwner(Integer id) {
        getOwnerOrThrow(id);
        userManageService.unlockUser(id);
    }
}
