package com.vti.springdatajpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminStatisticsRevenueDTO {
    private List<String> labels;
    private List<Number> revenue;
    private List<Integer> orderCount;
}
