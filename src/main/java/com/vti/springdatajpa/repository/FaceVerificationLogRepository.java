package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.FaceVerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaceVerificationLogRepository extends JpaRepository<FaceVerificationLog, Long> {

    List<FaceVerificationLog> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
