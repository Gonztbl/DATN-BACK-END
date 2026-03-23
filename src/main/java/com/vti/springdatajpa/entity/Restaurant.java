package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "logo_base64", columnDefinition = "LONGTEXT")
    private String logoBase64;

    @Column(nullable = false)
    private Boolean status = true; // true = open, false = closed

    @Column(name = "product_count")
    private Integer productCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(columnDefinition = "TEXT")
    private String schedule;

    @OneToMany(mappedBy = "restaurant")
    private List<Product> products;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Generate ID if not set
        if (id == null || id.isEmpty()) {
            id = generateRestaurantId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateRestaurantId() {
        // Generate ID like RS-9021
        return "RS-" + System.currentTimeMillis() % 10000;
    }
}
