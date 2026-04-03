package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.WalletDailySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WalletDailySnapshotRepository extends JpaRepository<WalletDailySnapshot, Long> {

    // Get snapshot for a specific date
    Optional<WalletDailySnapshot> findByWallet_IdAndRecordDate(Integer walletId, LocalDate recordDate);

    // Get snapshots for last N days
    List<WalletDailySnapshot> findByWalletIdAndRecordDateGreaterThanEqualOrderByRecordDateAsc(
            Integer walletId,
            LocalDate fromDate
    );

    // Get last snapshot
    Optional<WalletDailySnapshot> findFirstByWalletIdOrderByRecordDateDesc(Integer walletId);

    // Get snapshots between dates
    @Query("SELECT wds FROM WalletDailySnapshot wds WHERE wds.wallet.id = :walletId " +
           "AND wds.recordDate >= :fromDate AND wds.recordDate <= :toDate " +
           "ORDER BY wds.recordDate ASC")
    List<WalletDailySnapshot> findSnapshotsBetweenDates(
            @Param("walletId") Integer walletId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // Count snapshots with low balance (< 50k) for a wallet
    @Query("SELECT COUNT(wds) FROM WalletDailySnapshot wds " +
           "WHERE wds.wallet.id = :walletId AND wds.recordDate >= :fromDate " +
           "AND CAST(wds.endOfDayBalance AS java.math.BigDecimal) < 50000")
    Long countLowBalanceDays(@Param("walletId") Integer walletId, @Param("fromDate") LocalDate fromDate);

    // Calculate average balance for a wallet
    @Query("SELECT AVG(CAST(wds.endOfDayBalance AS DOUBLE)) FROM WalletDailySnapshot wds " +
           "WHERE wds.wallet.id = :walletId AND wds.recordDate >= :fromDate")
    Double getAverageBalance(@Param("walletId") Integer walletId, @Param("fromDate") LocalDate fromDate);

    // Calculate standard deviation of balance for a wallet
    @Query("SELECT SQRT(AVG(POWER(CAST(wds.endOfDayBalance AS DOUBLE) - " +
           "(SELECT AVG(CAST(wds2.endOfDayBalance AS DOUBLE)) FROM WalletDailySnapshot wds2 " +
           "WHERE wds2.wallet.id = :walletId AND wds2.recordDate >= :fromDate), 2))) " +
           "FROM WalletDailySnapshot wds " +
           "WHERE wds.wallet.id = :walletId AND wds.recordDate >= :fromDate")
    Double getBalanceStdDev(@Param("walletId") Integer walletId, @Param("fromDate") LocalDate fromDate);
}
