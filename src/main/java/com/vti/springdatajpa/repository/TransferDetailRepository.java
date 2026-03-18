package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.TransferDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferDetailRepository extends JpaRepository<TransferDetail, Integer> {
    void deleteByTransactionId(Integer transactionId);
}
