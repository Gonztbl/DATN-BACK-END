package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.ChartDataDTO;
import com.vti.springdatajpa.dto.DashboardStatsDTO;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.repository.OrderRepository;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public DashboardStatsDTO getStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        long totalUsers = userRepository.count();
        long newUsersToday = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        long totalWallets = walletRepository.count();
        long activeWallets = walletRepository.countByStatus(WalletStatus.ACTIVE);

        long totalTransactions = transactionRepository.count();
        long transactionsToday = transactionRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        Double totalRev = transactionRepository.sumAmount();
        BigDecimal totalRevenue = totalRev != null ? BigDecimal.valueOf(totalRev) : BigDecimal.ZERO;

        Double todayRev = transactionRepository.sumAmountBetween(startOfDay, endOfDay);
        BigDecimal revenueToday = todayRev != null ? BigDecimal.valueOf(todayRev) : BigDecimal.ZERO;

        long totalOrders = orderRepository.count();
        long ordersToday = orderRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .newUsersToday(newUsersToday)
                .totalWallets(totalWallets)
                .activeWallets(activeWallets)
                .totalTransactions(totalTransactions)
                .transactionsToday(transactionsToday)
                .totalRevenue(totalRevenue)
                .revenueToday(revenueToday)
                .totalOrders(totalOrders)
                .ordersToday(ordersToday)
                .build();
    }

    public ChartDataDTO getCharts(String period, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<ChartDataDTO.RevenueData> revData = new ArrayList<>();
        List<ChartDataDTO.TransactionData> transData = new ArrayList<>();
        List<ChartDataDTO.UserData> userData = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Double dailyRev = transactionRepository.sumAmountBetween(startOfDay, endOfDay);
            revData.add(new ChartDataDTO.RevenueData(date.toString(), dailyRev != null ? BigDecimal.valueOf(dailyRev) : BigDecimal.ZERO));

            long dailyTrans = transactionRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            transData.add(new ChartDataDTO.TransactionData(date.toString(), dailyTrans));

            long dailyUsers = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            userData.add(new ChartDataDTO.UserData(date.toString(), dailyUsers));
        }

        return ChartDataDTO.builder()
                .revenue(revData)
                .transactions(transData)
                .newUsers(userData)
                .build();
    }
}
