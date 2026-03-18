package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    void deleteByUserId(Integer userId);
}
