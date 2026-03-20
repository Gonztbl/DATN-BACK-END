package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.AdminAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, Integer> {
    Page<AdminAction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
