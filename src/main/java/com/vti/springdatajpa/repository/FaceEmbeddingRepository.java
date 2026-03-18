package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.FaceEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaceEmbeddingRepository extends JpaRepository<FaceEmbedding, Long> {

    List<FaceEmbedding> findByUserId(Integer userId);

    List<FaceEmbedding> findByUserIdAndPose(Integer userId, String pose);

    long countByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
