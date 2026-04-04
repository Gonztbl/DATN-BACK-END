package com.vti.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "WalletDailySnapshot")
@Table(name = "wallet_daily_snapshots", indexes = {
        @Index(name = "idx_wallet_id", columnList = "wallet_id"),
        @Index(name = "idx_record_date", columnList = "record_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDailySnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnore
    private Wallet wallet;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "end_of_day_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal endOfDayBalance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
