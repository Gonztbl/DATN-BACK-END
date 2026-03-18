package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.ApiError;
import com.vti.springdatajpa.dto.ApiResponse;
import com.vti.springdatajpa.dto.ForgotPasswordRequest;
import com.vti.springdatajpa.dto.LoginRequest;
import com.vti.springdatajpa.dto.LoginResponse;
import com.vti.springdatajpa.dto.RegisterResponse;
import com.vti.springdatajpa.dto.VerifyOtpRequest;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.form.AdminRegisterForm;
import com.vti.springdatajpa.form.RegisterForm;
import com.vti.springdatajpa.service.AuthService;
import com.vti.springdatajpa.service.EmailService;
import com.vti.springdatajpa.service.OtpService;
import com.vti.springdatajpa.service.RegisterService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterForm registerForm) {
        User user = modelMapper.map(registerForm, User.class);
        User savedUser = registerService.createAccount(user);
        
        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully");
        response.setUserId(savedUser.getId());
        response.setAccountNumber(user.getPhone()); // Số tài khoản = số điện thoại
        response.setWalletId("WALLET" + savedUser.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> adminRegister(@Valid @RequestBody AdminRegisterForm adminRegisterForm) {
        // Validate role - only allow specific roles
        if (adminRegisterForm.getRole() == null || 
            adminRegisterForm.getRole() == Role.USER) {
            throw new RuntimeException("Invalid role. Must be ADMIN, SUPPORT, RESTAURANT_OWNER, or SHIPPER");
        }
        
        User user = modelMapper.map(adminRegisterForm, User.class);
        user.setRole(adminRegisterForm.getRole());
        User savedUser = registerService.createAccountWithRole(user);
        
        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully with role " + adminRegisterForm.getRole().name());
        response.setUserId(savedUser.getId());
        response.setAccountNumber(user.getPhone());
        response.setWalletId("WALLET" + savedUser.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // 1. Kiểm tra email tồn tại trong hệ thống
        // 2. Tạo OTP ngẫu nhiên (6 chữ số)
        String otp = otpService.generateOtp(request.getEmail(), "RESET_PASSWORD");
        // 3. Gửi OTP qua email
        emailService.sendOtpEmail(request.getEmail(), otp);
        return ResponseEntity.ok(new ApiResponse("OTP đã được gửi đến email của bạn"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtpCode(), request.getPurpose());
        if (valid) {
            // Tạo token tạm thời hoặc đánh dấu đã xác thực
            return ResponseEntity.ok(new ApiResponse("Xác thực OTP thành công"));
        } else {
            return ResponseEntity.badRequest().body(new ApiError("OTP không hợp lệ hoặc đã hết hạn"));
        }
    }
}
