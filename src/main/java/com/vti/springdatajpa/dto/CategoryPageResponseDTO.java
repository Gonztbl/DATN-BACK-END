package com.vti.springdatajpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryPageResponseDTO {
    private List<CategoryDetailDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
