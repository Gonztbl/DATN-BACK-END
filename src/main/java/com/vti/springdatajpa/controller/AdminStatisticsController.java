package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminStatisticsOverviewDTO;
import com.vti.springdatajpa.dto.AdminStatisticsRevenueDTO;
import com.vti.springdatajpa.dto.AdminTopProductDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;
    private final UserRepository userRepository;

    /**
     * GET /api/admin/statistics/overview - Get overview statistics
     */
    @GetMapping("/overview")
    public ResponseEntity<AdminStatisticsOverviewDTO> getOverview(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        verifyAdminAccess();

        LocalDate from = fromDate != null && !fromDate.isEmpty() ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null && !toDate.isEmpty() ? LocalDate.parse(toDate) : null;

        AdminStatisticsOverviewDTO stats = adminStatisticsService.getOverviewStatistics(from, to);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/statistics/revenue - Get revenue statistics
     */
    @GetMapping("/revenue")
    public ResponseEntity<AdminStatisticsRevenueDTO> getRevenue(
            @RequestParam(defaultValue = "day") String groupBy,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        verifyAdminAccess();

        LocalDate from = fromDate != null && !fromDate.isEmpty() ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null && !toDate.isEmpty() ? LocalDate.parse(toDate) : null;

        AdminStatisticsRevenueDTO stats = adminStatisticsService.getRevenueStatistics(groupBy, from, to);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/statistics/top-products - Get top selling products
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<AdminTopProductDTO>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "quantity") String sortBy,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        verifyAdminAccess();

        LocalDate from = fromDate != null && !fromDate.isEmpty() ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null && !toDate.isEmpty() ? LocalDate.parse(toDate) : null;

        List<AdminTopProductDTO> products = adminStatisticsService.getTopProducts(limit, sortBy, from, to);
        return ResponseEntity.ok(products);
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
}
