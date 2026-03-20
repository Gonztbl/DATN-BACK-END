package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Integer> {
}
