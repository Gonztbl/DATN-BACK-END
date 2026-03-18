package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.OTPRequest;
import com.vti.springdatajpa.repository.OtpRequestRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRequestRepository otpRequestRepository;
    private final UserRepository userRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom random = new SecureRandom();

    /**
     * Tạo và lưu OTP
     * @param email Email của người dùng
     * @param purpose Mục đích sử dụng OTP (RESET_PASSWORD, EMAIL_VERIFICATION, etc.)
     * @return OTP string
     */
    public String generateOtp(String email, String purpose) {
        // Kiểm tra email tồn tại
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không tồn tại trong hệ thống");
        }

        // Tạo OTP ngẫu nhiên
        String otp = generateRandomOtp();

        // Lưu OTP vào database
        OTPRequest otpRequest = new OTPRequest();
        otpRequest.setUser(userRepository.findByEmail(email).get());
        otpRequest.setPurpose(purpose);
        otpRequest.setOtpCode(otp);
        otpRequest.setUsed(false);
        otpRequest.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpRequest.setCreatedAt(LocalDateTime.now());

        otpRequestRepository.save(otpRequest);

        return otp;
    }

    /**
     * Xác thực OTP
     * @param email Email của người dùng
     * @param otpCode Mã OTP cần xác thực
     * @param purpose Mục đích sử dụng OTP
     * @return true nếu OTP hợp lệ, false nếu không
     */
    public boolean verifyOtp(String email, String otpCode, String purpose) {
        Optional<OTPRequest> otpRequestOpt = otpRequestRepository
                .findByUserEmailAndPurposeAndIsUsedFalse(email, purpose);

        if (otpRequestOpt.isEmpty()) {
            return false;
        }

        OTPRequest otpRequest = otpRequestOpt.get();

        // Kiểm tra OTP có khớp và chưa hết hạn
        if (!otpRequest.getOtpCode().equals(otpCode)) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpRequest.getExpiresAt())) {
            return false;
        }

        // Đánh dấu OTP đã sử dụng
        otpRequest.setUsed(true);
        otpRequestRepository.save(otpRequest);

        return true;
    }

    /**
     * Tạo OTP ngẫu nhiên 6 chữ số
     */
    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Hủy tất cả OTP chưa sử dụng của email
     * @param email Email của người dùng
     */
    public void invalidateAllOtpForEmail(String email) {
        otpRequestRepository.invalidateAllOtpForEmail(email);
    }
}
