package com.vti.springdatajpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewListDTO {
    private Integer total;
    private List<ReviewDTO> reviews;
}
