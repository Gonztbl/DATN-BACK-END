package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<SupportTicket> findAll(Pageable pageable);
    Page<SupportTicket> findByStatus(SupportTicket.TicketStatus status, Pageable pageable);

    void deleteByUserId(Integer userId);
}
