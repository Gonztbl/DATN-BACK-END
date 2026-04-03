package com.vti.springdatajpa.entity;

import com.vti.springdatajpa.entity.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_requests", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_final_status", columnList = "final_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "term", nullable = false)
    private Integer term; // Loan term in months

    @Column(name = "purpose", length = 255)
    private String purpose;

    @Column(name = "declared_income", precision = 19, scale = 2)
    private BigDecimal declaredIncome;

    @Column(name = "job_segment_num", length = 100)
    private String jobSegmentNum;

    @Column(name = "ai_score")
    private Double aiScore; // AI decision score (probability)

    @Column(name = "ai_decision")
    private String aiDecision; // e.g., "REJECTED_BY_AI", "PASSED_AI"

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin; // Admin who reviewed the request

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false, length = 20)
    private LoanStatus finalStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
