package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    List<Category> findAll();
    
    Page<Category> findAll(Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE " +
           "(:search IS NULL OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(c.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Category> findCategoriesWithSearch(@Param("search") String search, Pageable pageable);
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.id != :excludeId")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("excludeId") Integer excludeId);
    
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.categoryId = :categoryId")
    boolean hasProducts(@Param("categoryId") Integer categoryId);
    
    Optional<Category> findById(Integer id);
}
