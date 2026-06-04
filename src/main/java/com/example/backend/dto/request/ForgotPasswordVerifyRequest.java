package com.example.backend.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordVerifyRequest {
    private String email;
    private String otp;
}
