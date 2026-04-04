package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.dto.AdminDashboardStatsDTO;
import com.vti.springdatajpa.dto.LoanRequestListDTO;
import com.vti.springdatajpa.entity.LoanRequest;
import com.vti.springdatajpa.entity.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    // Get by ID
    Optional<LoanRequest> findById(Long id);

    // Get all loans for a user
    List<LoanRequest> findByUser_IdOrderByCreatedAtDesc(Integer userId);

    // Get paginated loans for a user
    Page<LoanRequest> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    // Get pending admin review loans (with pagination)
    Page<LoanRequest> findByFinalStatusOrderByCreatedAtDesc(LoanStatus status, Pageable pageable);

    @Query("SELECT new com.vti.springdatajpa.dto.LoanRequestListDTO(" +
           "l.id, l.amount, l.aiScore, l.finalStatus, u.id, u.fullName, l.createdAt) " +
           "FROM LoanRequest l JOIN l.user u " +
           "WHERE l.finalStatus = :status " +
           "AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minAiScore IS NULL OR l.aiScore >= :minAiScore) " +
           "AND (:maxAiScore IS NULL OR l.aiScore <= :maxAiScore)")
    Page<LoanRequestListDTO> findPendingAdminLoanListDTO(
            @Param("status") LoanStatus status,
            @Param("keyword") String keyword,
            @Param("minAiScore") Double minAiScore,
            @Param("maxAiScore") Double maxAiScore,
            Pageable pageable);

    @Query("SELECT new com.vti.springdatajpa.dto.LoanRequestListDTO(" +
           "l.id, l.amount, l.aiScore, l.finalStatus, u.id, u.fullName, l.createdAt) " +
           "FROM LoanRequest l JOIN l.user u " +
           "WHERE (:status IS NULL OR l.finalStatus = :status) " +
           "AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minAiScore IS NULL OR l.aiScore >= :minAiScore) " +
           "AND (:maxAiScore IS NULL OR l.aiScore <= :maxAiScore)")
    Page<LoanRequestListDTO> findAllLoansListDTO(
            @Param("keyword") String keyword,
            @Param("minAiScore") Double minAiScore,
            @Param("maxAiScore") Double maxAiScore,
            @Param("status") LoanStatus status,
            Pageable pageable);

    // Get by user and loan status
    List<LoanRequest> findByUser_IdAndFinalStatusOrderByCreatedAtDesc(Integer userId, LoanStatus status);

    @Query("SELECT new com.vti.springdatajpa.dto.AdminDashboardStatsDTO(" +
            "COUNT(l), " +
            "COALESCE(AVG(CAST(l.amount AS double)), 0.0), " +
            "COALESCE(SUM(CASE WHEN l.aiScore >= 0.30 THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN l.aiScore >= 0.10 AND l.aiScore < 0.30 THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN l.aiScore < 0.10 THEN 1L ELSE 0L END), 0L)) " +
            "FROM LoanRequest l WHERE l.finalStatus = :status")
    AdminDashboardStatsDTO findAdminDashboardStats(@Param("status") LoanStatus status);

    // Get loan by ID with details (eager fetch)
    @Query("SELECT lr FROM LoanRequest lr LEFT JOIN FETCH lr.user LEFT JOIN FETCH lr.admin " +
           "WHERE lr.id = :id")
    Optional<LoanRequest> findByIdWithDetails(@Param("id") Long id);

    // Get all pending admin review loans
    @Query("SELECT lr FROM LoanRequest lr WHERE lr.finalStatus = com.vti.springdatajpa.entity.enums.LoanStatus.PENDING_ADMIN " +
           "ORDER BY lr.createdAt ASC")
    List<LoanRequest> findPendingAdminReview();

    // Count loans by user and status
    Long countByUser_IdAndFinalStatus(Integer userId, LoanStatus status);
}
