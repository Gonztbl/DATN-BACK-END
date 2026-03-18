package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.OTPRequest;
import com.vti.springdatajpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRequestRepository extends JpaRepository<OTPRequest, Integer> {

    /**
     * Tìm OTP theo email, purpose và chưa sử dụng
     */
    Optional<OTPRequest> findByUserEmailAndPurposeAndIsUsedFalse(String email, String purpose);

    /**
     * Tìm tất cả OTP của user theo purpose
     */
    List<OTPRequest> findByUserIdAndPurpose(Integer userId, String purpose);

    /**
     * Hủy tất cả OTP chưa sử dụng của email
     */
    @Modifying
    @Query("UPDATE OTPRequest o SET o.isUsed = true WHERE o.user.email = :email AND o.isUsed = false")
    void invalidateAllOtpForEmail(@Param("email") String email);

    /**
     * Xóa các OTP đã hết hạn
     */
    @Modifying
    @Query("DELETE FROM OTPRequest o WHERE o.expiresAt < :now")
    void deleteExpiredOtp(@Param("now") LocalDateTime now);

    /**
     * Đếm số OTP còn hiệu lực của email
     */
    @Query("SELECT COUNT(o) FROM OTPRequest o WHERE o.user.email = :email AND o.isUsed = false AND o.expiresAt > :now")
    long countValidOtpByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    void deleteByUserId(Integer userId);
}
