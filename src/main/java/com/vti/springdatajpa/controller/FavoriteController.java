package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.FavoriteResponseDTO;
import com.vti.springdatajpa.entity.Favorite;
import com.vti.springdatajpa.entity.Restaurant;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.FavoriteRepository;
import com.vti.springdatajpa.repository.RestaurantRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /**
     * POST /api/favorites/restaurants/{id} - Add restaurant to favorites
     */
    @PostMapping("/restaurants/{id}")
    public ResponseEntity<FavoriteResponseDTO> addFavorite(@PathVariable String id) {
        Integer userId = getCurrentUserId();

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndRestaurantId(userId, id)) {
            throw new RuntimeException("Restaurant already in favorites");
        }

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRestaurant(restaurant);
        favorite.setCreatedAt(LocalDateTime.now());

        Favorite saved = favoriteRepository.save(favorite);
        return ResponseEntity.status(201).body(mapToDTO(saved));
    }

    /**
     * DELETE /api/favorites/restaurants/{id} - Remove restaurant from favorites
     */
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String id) {
        Integer userId = getCurrentUserId();

        Favorite favorite = favoriteRepository.findByUserIdAndRestaurantId(userId, id)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.delete(favorite);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/favorites/restaurants - Get list of favorite restaurants
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites() {
        Integer userId = getCurrentUserId();
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        List<FavoriteResponseDTO> dtos = favorites.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type");
        }

        return userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private FavoriteResponseDTO mapToDTO(Favorite favorite) {
        FavoriteResponseDTO dto = new FavoriteResponseDTO();
        dto.setId(favorite.getId());
        dto.setRestaurantId(favorite.getRestaurant().getId());
        dto.setRestaurantName(favorite.getRestaurant().getName());
        dto.setLogoBase64(favorite.getRestaurant().getLogoBase64());
        dto.setAddress(favorite.getRestaurant().getAddress());
        dto.setFavoritedAt(favorite.getCreatedAt());
        return dto;
    }
}
