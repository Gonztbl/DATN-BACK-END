package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    
    Page<Restaurant> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE " +
           "r.deletedAt IS NULL AND " +
           "(:search IS NULL OR " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "r.id LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Restaurant> findRestaurantsWithFilters(@Param("search") String search, 
                                                @Param("status") Boolean status, 
                                                Pageable pageable);
    
    Optional<Restaurant> findByIdAndDeletedAtIsNull(String id);
    
    List<Restaurant> findByOwnerIdAndDeletedAtIsNull(Integer ownerId);
    
    boolean existsByNameAndDeletedAtIsNull(String name);
    
    @Query("SELECT COUNT(r) > 0 FROM Restaurant r WHERE r.name = :name AND r.id != :excludeId AND r.deletedAt IS NULL")
    boolean existsByNameAndIdNotAndDeletedAtIsNull(@Param("name") String name, @Param("excludeId") String excludeId);
    
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.restaurantId = :restaurantId")
    boolean hasProducts(@Param("restaurantId") String restaurantId);

    @Modifying
    @Transactional
    @Query("UPDATE Restaurant r SET r.ownerId = NULL WHERE r.ownerId = :ownerId")
    void setOwnerIdNull(@Param("ownerId") Integer ownerId);
}
