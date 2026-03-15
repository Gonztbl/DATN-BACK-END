package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Order;
import com.vti.springdatajpa.entity.OrderItem;
import com.vti.springdatajpa.entity.Product;
import com.vti.springdatajpa.repository.OrderItemRepository;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.ProductRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Get overview statistics for admin dashboard
     */
    public AdminStatisticsOverviewDTO getOverviewStatistics(LocalDate fromDate, LocalDate toDate) {
        List<Order> allOrders = orderRepository.findAll();
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Filter orders by date range
        List<Order> filteredOrders = allOrders.stream()
                .filter(o -> fromDateTime == null || !o.getCreatedAt().isBefore(fromDateTime))
                .filter(o -> toDateTime == null || !o.getCreatedAt().isAfter(toDateTime))
                .collect(Collectors.toList());

        AdminStatisticsOverviewDTO dto = new AdminStatisticsOverviewDTO();

        // Total orders
        dto.setTotalOrders((long) filteredOrders.size());

        // Total revenue (only completed orders)
        BigDecimal totalRevenue = filteredOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalRevenue(totalRevenue);

        // Average order value
        long completedOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .count();
        if (completedOrders > 0) {
            dto.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            dto.setAverageOrderValue(BigDecimal.ZERO);
        }

        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = filteredOrders.stream()
                    .filter(o -> o.getStatus() == status)
                    .count();
            ordersByStatus.put(status.name(), count);
        }
        dto.setOrdersByStatus(ordersByStatus);

        // Today's stats
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);

        List<Order> todayOrders = allOrders.stream()
                .filter(o -> !o.getCreatedAt().isBefore(todayStart) && !o.getCreatedAt().isAfter(todayEnd))
                .collect(Collectors.toList());

        dto.setOrdersToday(todayOrders.size());
        dto.setRevenueToday(todayOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // New users today
        long newUsersToday = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null)
                .filter(u -> !u.getCreatedAt().isBefore(todayStart) && !u.getCreatedAt().isAfter(todayEnd))
                .count();
        dto.setNewUsersToday((int) newUsersToday);

        // Top restaurants
        List<AdminStatisticsOverviewDTO.TopRestaurantDTO> topRestaurants = getTopRestaurants(filteredOrders, 5);
        dto.setTopRestaurants(topRestaurants);

        return dto;
    }

    /**
     * Get revenue statistics grouped by day/week/month
     */
    public AdminStatisticsRevenueDTO getRevenueStatistics(String groupBy, LocalDate fromDate, LocalDate toDate) {
        List<Order> allOrders = orderRepository.findAll();

        LocalDate start = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        List<String> labels = new ArrayList<>();
        List<Number> revenue = new ArrayList<>();
        List<Integer> orderCount = new ArrayList<>();

        if ("month".equalsIgnoreCase(groupBy)) {
            // Group by month
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
                LocalDate monthStart = date.withDayOfMonth(1);
                LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());
                String label = date.format(formatter);
                labels.add(label);

                BigDecimal monthRevenue = calculateRevenue(allOrders, monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59));
                int monthOrders = countOrders(allOrders, monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59));

                revenue.add(monthRevenue);
                orderCount.add(monthOrders);
            }
        } else if ("week".equalsIgnoreCase(groupBy)) {
            // Group by week
            for (LocalDate date = start; !date.isAfter(end); date = date.plusWeeks(1)) {
                LocalDate weekStart = date;
                LocalDate weekEnd = date.plusDays(6);
                String label = date.toString();
                labels.add(label);

                BigDecimal weekRevenue = calculateRevenue(allOrders, weekStart.atStartOfDay(), weekEnd.atTime(23, 59, 59));
                int weekOrders = countOrders(allOrders, weekStart.atStartOfDay(), weekEnd.atTime(23, 59, 59));

                revenue.add(weekRevenue);
                orderCount.add(weekOrders);
            }
        } else {
            // Group by day (default)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                String label = date.format(formatter);
                labels.add(label);

                BigDecimal dayRevenue = calculateRevenue(allOrders, date.atStartOfDay(), date.atTime(23, 59, 59));
                int dayOrders = countOrders(allOrders, date.atStartOfDay(), date.atTime(23, 59, 59));

                revenue.add(dayRevenue);
                orderCount.add(dayOrders);
            }
        }

        AdminStatisticsRevenueDTO dto = new AdminStatisticsRevenueDTO();
        dto.setLabels(labels);
        dto.setRevenue(revenue);
        dto.setOrderCount(orderCount);
        return dto;
    }

    /**
     * Get top selling products
     */
    public List<AdminTopProductDTO> getTopProducts(int limit, String sortBy, LocalDate fromDate, LocalDate toDate) {
        List<OrderItem> allOrderItems = orderItemRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        // Filter by order date
        Set<Integer> validOrderIds = allOrders.stream()
                .filter(o -> fromDateTime == null || !o.getCreatedAt().isBefore(fromDateTime))
                .filter(o -> toDateTime == null || !o.getCreatedAt().isAfter(toDateTime))
                .map(Order::getId)
                .collect(Collectors.toSet());

        // Aggregate by product
        Map<Integer, ProductStats> productStatsMap = new HashMap<>();

        for (OrderItem item : allOrderItems) {
            if (!validOrderIds.contains(item.getOrderId())) {
                continue;
            }

            ProductStats stats = productStatsMap.computeIfAbsent(item.getProductId(), k -> new ProductStats());
            stats.quantity += item.getQuantity();
            stats.revenue = stats.revenue.add(item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())));

            // Get product name
            if (stats.name == null && item.getProduct() != null) {
                stats.name = item.getProduct().getName();
            }
        }

        // Convert to DTOs
        List<AdminTopProductDTO> result = productStatsMap.entrySet().stream()
                .map(entry -> {
                    AdminTopProductDTO dto = new AdminTopProductDTO();
                    dto.setProductId(entry.getKey());
                    dto.setQuantitySold(entry.getValue().quantity);
                    dto.setRevenue(entry.getValue().revenue);

                    // Get product name from repository if not set
                    if (entry.getValue().name == null) {
                        Optional<Product> product = productRepository.findById(entry.getKey());
                        dto.setProductName(product.map(Product::getName).orElse("Unknown"));
                    } else {
                        dto.setProductName(entry.getValue().name);
                    }
                    return dto;
                })
                .sorted((a, b) -> {
                    if ("revenue".equalsIgnoreCase(sortBy)) {
                        return b.getRevenue().compareTo(a.getRevenue());
                    } else {
                        return b.getQuantitySold().compareTo(a.getQuantitySold());
                    }
                })
                .limit(limit)
                .collect(Collectors.toList());

        return result;
    }

    private List<AdminStatisticsOverviewDTO.TopRestaurantDTO> getTopRestaurants(List<Order> orders, int limit) {
        Map<String, RestaurantStats> statsMap = new HashMap<>();

        for (Order order : orders) {
            if (order.getRestaurantId() == null) continue;

            RestaurantStats stats = statsMap.computeIfAbsent(order.getRestaurantId(), k -> new RestaurantStats());
            stats.id = order.getRestaurantId();
            stats.orderCount++;
            stats.revenue = stats.revenue.add(order.getTotalAmount());

            if (stats.name == null && order.getRestaurant() != null) {
                stats.name = order.getRestaurant().getName();
            }
        }

        return statsMap.values().stream()
                .sorted((a, b) -> b.revenue.compareTo(a.revenue))
                .limit(limit)
                .map(s -> {
                    AdminStatisticsOverviewDTO.TopRestaurantDTO dto = new AdminStatisticsOverviewDTO.TopRestaurantDTO();
                    dto.setRestaurantId(s.id);
                    dto.setRestaurantName(s.name != null ? s.name : s.id);
                    dto.setOrderCount(s.orderCount);
                    dto.setRevenue(s.revenue);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateRevenue(List<Order> orders, LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int countOrders(List<Order> orders, LocalDateTime start, LocalDateTime end) {
        return (int) orders.stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .count();
    }

    private static class ProductStats {
        String name;
        int quantity;
        BigDecimal revenue = BigDecimal.ZERO;
    }

    private static class RestaurantStats {
        String id;
        String name;
        long orderCount;
        BigDecimal revenue = BigDecimal.ZERO;
    }
}
