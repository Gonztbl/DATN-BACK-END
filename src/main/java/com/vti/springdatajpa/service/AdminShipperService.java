package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.DailyShipperStatsDTO;
import com.vti.springdatajpa.dto.ShipperDetailDTO;
import com.vti.springdatajpa.dto.ShipperStatisticsDTO;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import com.vti.springdatajpa.repository.ShipperProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminShipperService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserManageService userManageService;
    private final ShipperProfileRepository shipperProfileRepository;

    private User getShipperOrThrow(Integer shipperId) {
        User user = userRepository.findById(shipperId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipper not found"));
        if (user.getRole() != Role.SHIPPER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a shipper");
        }
        return user;
    }

    public ShipperDetailDTO getShipperDetail(Integer id) {
        User user = getShipperOrThrow(id);

        long totalOrders = orderRepository.countByShipperId(id);
        long completedOrders = orderRepository.countByShipperIdAndStatus(id, Order.OrderStatus.COMPLETED);
        long failedOrders = orderRepository.countByShipperIdAndStatus(id, Order.OrderStatus.DELIVERY_FAILED);

        BigDecimal earnings = BigDecimal.ZERO;
        Optional<Wallet> walletOpt = walletRepository.findByUserId(id);
        if (walletOpt.isPresent()) {
            Double val = transactionRepository.sumAmountByWalletIdAndDirection(walletOpt.get().getId(), TransactionDirection.IN);
            if (val != null) {
                earnings = BigDecimal.valueOf(val);
            }
        }

        ShipperStatisticsDTO statDTO = ShipperStatisticsDTO.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .failedOrders(failedOrders)
                .totalEarnings(earnings)
                .avgRating(5.0) // Mocking avgRating as not stored
                .build();

        return ShipperDetailDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .statistics(statDTO)
                .build();
    }

    public Page<Order> getShipperOrders(Integer id, String statusString, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        getShipperOrThrow(id);
        
        Order.OrderStatus status = null;
        if (statusString != null && !statusString.isBlank()) {
            try {
                status = Order.OrderStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
            }
        }

        // Default date range if not provided
        if (fromDate == null) fromDate = LocalDate.of(2000, 1, 1);
        if (toDate == null) toDate = LocalDate.now();

        LocalDateTime startDt = fromDate.atStartOfDay();
        LocalDateTime endDt = toDate.atTime(LocalTime.MAX);

        if (status != null) {
            return orderRepository.findByShipperIdAndStatusAndCreatedAtBetween(id, status, startDt, endDt, pageable);
        } else {
            return orderRepository.findByShipperIdAndCreatedAtBetween(id, startDt, endDt, pageable);
        }
    }

    public ShipperStatisticsDTO getShipperStatistics(Integer id, LocalDate fromDate, LocalDate toDate) {
        User user = getShipperOrThrow(id);
        
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();

        LocalDateTime startDt = fromDate.atStartOfDay();
        LocalDateTime endDt = toDate.atTime(LocalTime.MAX);

        long totalOrders = orderRepository.countByShipperIdAndCreatedAtBetween(id, startDt, endDt);
        long completedOrders = orderRepository.countByShipperIdAndStatusAndCreatedAtBetween(id, Order.OrderStatus.COMPLETED, startDt, endDt);
        long failedOrders = orderRepository.countByShipperIdAndStatusAndCreatedAtBetween(id, Order.OrderStatus.DELIVERY_FAILED, startDt, endDt);

        BigDecimal totalEarnings = BigDecimal.ZERO;
        Integer walletId = walletRepository.findByUserId(id).map(Wallet::getId).orElse(null);

        if (walletId != null) {
            Double val = transactionRepository.sumAmountByWalletIdAndDirectionBetween(walletId, TransactionDirection.IN, startDt, endDt);
            if (val != null) {
                totalEarnings = BigDecimal.valueOf(val);
            }
        }

        List<DailyShipperStatsDTO> dailyStats = new ArrayList<>();
        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            LocalDateTime s = date.atStartOfDay();
            LocalDateTime e = date.atTime(LocalTime.MAX);
            
            long dOrders = orderRepository.countByShipperIdAndCreatedAtBetween(id, s, e);
            BigDecimal dEarnings = BigDecimal.ZERO;
            if (walletId != null) {
                Double val = transactionRepository.sumAmountByWalletIdAndDirectionBetween(walletId, TransactionDirection.IN, s, e);
                if (val != null) {
                    dEarnings = BigDecimal.valueOf(val);
                }
            }
            dailyStats.add(new DailyShipperStatsDTO(date.toString(), dOrders, dEarnings));
        }

        return ShipperStatisticsDTO.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .failedOrders(failedOrders)
                .totalEarnings(totalEarnings)
                .avgRating(5.0)
                .dailyStats(dailyStats)
                .build();
    }

    public Page<ShipperDetailDTO> getShippers(String search, Boolean isOnline, Pageable pageable) {
        Page<com.vti.springdatajpa.entity.User> users = shipperProfileRepository.findShippersWithFilters(search, isOnline, pageable);
        return users.map(this::mapToDetailDTOFromUser);
    }

    private ShipperDetailDTO mapToDetailDTOFromUser(com.vti.springdatajpa.entity.User user) {
        boolean isOnline = false;
        if (user.getShipperProfile() != null) {
            isOnline = user.getShipperProfile().getIsOnline();
        }

        return ShipperDetailDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .isOnline(isOnline)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void lockShipper(Integer id) {
        getShipperOrThrow(id);
        userManageService.lockUser(id);
    }

    public void unlockShipper(Integer id) {
        getShipperOrThrow(id);
        userManageService.unlockUser(id);
    }
}
