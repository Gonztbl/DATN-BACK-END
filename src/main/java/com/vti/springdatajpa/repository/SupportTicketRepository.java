package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Integer userId);

    void deleteByUserId(Integer userId);
}
