package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.ShipperProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperProfileRepository extends JpaRepository<ShipperProfile, Integer> {
    
    Optional<ShipperProfile> findByUserId(Integer userId);
    
    @Query("SELECT sp FROM ShipperProfile sp JOIN sp.user u WHERE " +
           "(:search IS NULL OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isOnline IS NULL OR sp.isOnline = :isOnline)")
    org.springframework.data.domain.Page<ShipperProfile> findShippersWithFilters(
            @Param("search") String search,
            @Param("isOnline") Boolean isOnline,
            org.springframework.data.domain.Pageable pageable);
    
    boolean existsByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
