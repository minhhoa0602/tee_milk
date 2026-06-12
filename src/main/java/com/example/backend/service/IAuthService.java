package com.example.backend.service;

import com.example.backend.dto.request.*;
import com.example.backend.dto.response.ResetPasswordRequest;
import com.example.backend.dto.response.ResetPasswordWithTokenRequest;
import com.example.backend.dto.response.TokenResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthService {

    //sinh ma ngau nhien 6 chu so
    String generateOtp();
    //ĐĂNG KÝ VÀ BẮN OTP EMAIL
    String register( RegisterRequest registerRequest);
    //KÍCH HOẠT TÀI KHOẢN QUA OTP
    String verifyAccount(VerifyRequest verifyRequest);
    //ĐĂNG NHẬP (KIỂM TRA TRẠNG THÁI VERIFIED)
    TokenResponse login(LoginRequest loginRequest);

    String requestForgotPassword(ForgotPasswordRequest request);
    String verifyForgotPasswordOtp(ForgotPasswordVerifyRequest request);
    String resetPasswordWithToken(ResetPasswordWithTokenRequest request);

    String logout(String bearerToken);
}
