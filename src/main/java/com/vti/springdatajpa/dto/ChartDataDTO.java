package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDTO {
    private List<RevenueData> revenue;
    private List<TransactionData> transactions;
    private List<UserData> newUsers;

    @Data
    @AllArgsConstructor
    public static class RevenueData {
        private String date;
        private BigDecimal value;
    }

    @Data
    @AllArgsConstructor
    public static class TransactionData {
        private String date;
        private long count;
    }

    @Data
    @AllArgsConstructor
    public static class UserData {
        private String date;
        private long count;
    }
}
