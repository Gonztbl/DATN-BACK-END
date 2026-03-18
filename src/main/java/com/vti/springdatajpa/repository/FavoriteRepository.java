package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserId(Integer userId);
    Optional<Favorite> findByUserIdAndRestaurantId(Integer userId, String restaurantId);
    void deleteByUserIdAndRestaurantId(Integer userId, String restaurantId);
    boolean existsByUserIdAndRestaurantId(Integer userId, String restaurantId);

    void deleteByUserId(Integer userId);
}
