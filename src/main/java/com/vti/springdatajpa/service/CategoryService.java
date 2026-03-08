package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Category;
import com.vti.springdatajpa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryPageResponseDTO getCategories(int page, int limit, String search, String sortBy, String sortDir) {
        // Convert 1-based page to 0-based for Spring Data
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        
        // Validate sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Create pageable with sort
        Pageable pageable = PageRequest.of(zeroBasedPage, limit, Sort.by(direction, sortBy));
        
        // Get categories with search
        Page<Category> categoryPage;
        if (search != null && !search.trim().isEmpty()) {
            categoryPage = categoryRepository.findCategoriesWithSearch(search.trim(), pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        
        // Convert to DTOs
        List<CategoryDetailDTO> categoryDTOs = categoryPage.getContent().stream()
                .map(category -> modelMapper.map(category, CategoryDetailDTO.class))
                .collect(Collectors.toList());
        
        // Build response
        CategoryPageResponseDTO response = new CategoryPageResponseDTO();
        response.setContent(categoryDTOs);
        response.setPageNumber(page); // Return original 1-based page
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setFirst(categoryPage.isFirst());
        response.setLast(categoryPage.isLast());
        
        return response;
    }

    public CategoryDetailDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        return modelMapper.map(category, CategoryDetailDTO.class);
    }

    public CategoryDetailDTO createCategory(CategoryCreateRequestDTO request) {
        // Check if name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category name already exists: " + request.getName());
        }
        
        Category category = modelMapper.map(request, Category.class);
        Category savedCategory = categoryRepository.save(category);
        
        return modelMapper.map(savedCategory, CategoryDetailDTO.class);
    }

    public CategoryDetailDTO updateCategory(Integer id, CategoryUpdateRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Check if name already exists (excluding current category)
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
                throw new RuntimeException("Category name already exists: " + request.getName());
            }
            category.setName(request.getName());
        }
        
        // Update other fields if provided
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getOrderIndex() != null) {
            category.setOrderIndex(request.getOrderIndex());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        
        return modelMapper.map(updatedCategory, CategoryDetailDTO.class);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Check if category has products
        if (categoryRepository.hasProducts(id)) {
            throw new RuntimeException("Cannot delete category because it has associated products");
        }
        
        categoryRepository.delete(category);
    }

    public CategoryCheckNameDTO checkCategoryName(String name, Integer excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = categoryRepository.existsByNameAndIdNot(name, excludeId);
        } else {
            exists = categoryRepository.existsByName(name);
        }
        
        CategoryCheckNameDTO response = new CategoryCheckNameDTO();
        response.setExists(exists);
        
        return response;
    }
}
