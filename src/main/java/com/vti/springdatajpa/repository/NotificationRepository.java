package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
    Page<Notification> findByUserIdAndIsReadFalse(Integer userId, Pageable pageable);
}
