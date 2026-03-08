package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Category;
import com.vti.springdatajpa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/categories-count")
    public String getCategoriesCount() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return "Total categories: " + categories.size();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/categories-list")
    public List<Category> getCategoriesList() {
        return categoryRepository.findAll();
    }
}
