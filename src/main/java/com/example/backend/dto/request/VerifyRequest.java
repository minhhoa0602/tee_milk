package com.example.backend.dto.request;

import lombok.Data;

@Data
public class VerifyRequest {
    private String email;
    private String otp;
}
