package com.example.backend.service;

public interface IEmailService {
    void sendOtpEmail(String toEmail, String otp);
}
