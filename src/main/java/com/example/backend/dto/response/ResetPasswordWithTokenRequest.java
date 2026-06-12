package com.example.backend.dto.response;

import lombok.Data;

@Data
public class ResetPasswordWithTokenRequest {
    private String email;
    private String resetToken; // Dùng cái này làm bằng chứng đã xác thực OTP thành công
    private String newPassword;
}