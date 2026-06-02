package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("TeeMilk <noreply@teemilk.com>");
        message.setTo(toEmail);
        message.setSubject("Mã xác thực tài khoản ứng dụng TeeMilk của bạn");
        message.setText("Chào bạn,\nMã OTP kích hoạt tài khoản của bạn là: " + otp + "\nMã này sẽ hết hạn sau 5 phút.");
        mailSender.send(message);
    }
}
