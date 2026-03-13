package com.vti.springdatajpa.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vti.springdatajpa.entity.FaceEmbedding;
import com.vti.springdatajpa.repository.FaceEmbeddingRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cache service for user face embeddings.
 * Uses Caffeine in-memory cache to avoid DB queries on every verification.
 *
 * Cache config:
 * - Max 1000 users
 * - TTL 10 minutes
 * - Evict on register/delete
 */
@Service
@Slf4j
public class FaceEmbeddingCacheService {

    @Autowired
    private FaceEmbeddingRepository embeddingRepository;

    private Cache<Integer, List<FaceEmbedding>> cache;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();
        log.info("Face embedding cache initialized (max=1000, ttl=10min)");
    }

    /**
     * Get embeddings for a user (cache-first, fallback to DB).
     */
    public List<FaceEmbedding> getEmbeddings(Integer userId) {
        return cache.get(userId, key -> {
            log.debug("Cache MISS for user {}, loading from DB", key);
            return embeddingRepository.findByUserId(key);
        });
    }

    /**
     * Evict cache for a specific user (on register/delete).
     */
    public void evict(Integer userId) {
        cache.invalidate(userId);
        log.debug("Cache evicted for user {}", userId);
    }

    /**
     * Evict all cache entries.
     */
    public void evictAll() {
        cache.invalidateAll();
        log.info("Cache cleared: all entries evicted");
    }
}
