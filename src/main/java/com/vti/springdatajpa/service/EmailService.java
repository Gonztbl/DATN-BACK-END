package com.vti.springdatajpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Gửi email đơn giản chứa mã OTP.
     * @param toEmail Địa chỉ email người nhận
     * @param otp Mã OTP cần gửi
     */
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP xác thực tài khoản SmartPay");
        message.setText("Xin chào,\n\nMã OTP của bạn là: " + otp + 
                    "\n\nMã có hiệu lực trong 5 phút." +
                    "\nVui lòng không chia sẻ mã này với bất kỳ ai." +
                    "\n\nTrân trọng,\nSmartPay Team");
        mailSender.send(message);
    }

    /**
     * Gửi email thông báo khóa tài khoản
     * @param toEmail Địa chỉ email người nhận
     * @param reason Lý do khóa
     */
    @Async
    public void sendAccountLockedEmail(String toEmail, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thông báo khóa tài khoản SmartPay");
        message.setText("Xin chào,\n\nTài khoản của bạn đã bị khóa." +
                    (reason != null ? "\nLý do: " + reason : "") +
                    "\n\nVui lòng liên hệ hỗ trợ để biết thêm chi tiết." +
                    "\n\nTrân trọng,\nSmartPay Team");
        mailSender.send(message);
    }

    /**
     * Gửi email thông báo mở khóa tài khoản
     * @param toEmail Địa chỉ email người nhận
     */
    @Async
    public void sendAccountUnlockedEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thông báo mở khóa tài khoản SmartPay");
        message.setText("Xin chào,\n\nTài khoản của bạn đã được mở khóa." +
                    "\n\nBạn có thể đăng nhập và sử dụng dịch vụ bình thường." +
                    "\n\nTrân trọng,\nSmartPay Team");
        mailSender.send(message);
    }
}
