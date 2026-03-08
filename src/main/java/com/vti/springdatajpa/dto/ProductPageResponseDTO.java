package com.vti.springdatajpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductPageResponseDTO {
    
    private List<ProductDetailDTO> data;
    
    private PaginationInfo pagination;
    
    @Data
    public static class PaginationInfo {
        private Long total;
        private Integer page;
        private Integer limit;
        private Integer totalPages;
    }
}
