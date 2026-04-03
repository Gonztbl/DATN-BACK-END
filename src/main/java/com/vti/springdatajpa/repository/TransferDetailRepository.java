package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.TransferDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransferDetailRepository extends JpaRepository<TransferDetail, Integer> {
    void deleteByTransactionId(Integer transactionId);

    // Count distinct receivers for a wallet in last N days
    @Query("SELECT COUNT(DISTINCT td.counterpartyWalletId) FROM TransferDetail td " +
           "WHERE td.transaction.wallet.id = :walletId " +
           "AND td.transaction.createdAt >= :fromDate " +
           "AND td.transaction.direction = com.vti.springdatajpa.entity.enums.TransactionDirection.OUT")
    Long countDistinctReceiversInRange(@Param("walletId") Integer walletId, @Param("fromDate") LocalDateTime fromDate);

    // Count peer-to-peer transfers (TRANSFER_OUT type) in last N days
    @Query("SELECT COUNT(td) FROM TransferDetail td " +
           "WHERE td.transaction.wallet.id = :walletId " +
           "AND td.transaction.type = com.vti.springdatajpa.entity.enums.TransactionType.TRANSFER_OUT " +
           "AND td.transaction.createdAt >= :fromDate")
    Long countPeerTransfersInRange(@Param("walletId") Integer walletId, @Param("fromDate") LocalDateTime fromDate);
}

