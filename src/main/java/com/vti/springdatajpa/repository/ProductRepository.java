package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByCategoryIdAndDeletedAtIsNull(Integer categoryId);
    
    @Query("SELECT p FROM Product p WHERE " +
           "p.deletedAt IS NULL AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findProductsByFilters(@Param("categoryId") Integer categoryId, 
                                       @Param("search") String search);
    
    @Query("SELECT p FROM Product p JOIN p.restaurant r WHERE p.deletedAt IS NULL AND r.status = true")
    List<Product> findAvailableProducts();
    
    // Admin queries
    @Query("SELECT p FROM Product p WHERE " +
           "p.deletedAt IS NULL AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:restaurantId IS NULL OR p.restaurantId = :restaurantId) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(p.id AS string) LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> findProductsWithFilters(@Param("categoryId") Integer categoryId, 
                                      @Param("restaurantId") String restaurantId,
                                      @Param("search") String search,
                                      @Param("status") String status,
                                      Pageable pageable);
    
    Optional<Product> findByIdAndDeletedAtIsNull(Integer id);
    
    boolean existsByNameAndDeletedAtIsNull(String name);
    
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.name = :name AND p.id != :excludeId AND p.deletedAt IS NULL")
    boolean existsByNameAndIdNotAndDeletedAtIsNull(@Param("name") String name, @Param("excludeId") Integer excludeId);
    
    // Alternative method for soft delete check
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findActiveProductById(@Param("id") Integer id);

    // Restaurant Owner methods
    Page<Product> findByRestaurantIdAndDeletedAtIsNull(String restaurantId, Pageable pageable);

    Page<Product> findByRestaurantIdAndStatusAndDeletedAtIsNull(String restaurantId, String status, Pageable pageable);

    Optional<Product> findByIdAndRestaurantIdAndDeletedAtIsNull(Integer id, String restaurantId);
}
