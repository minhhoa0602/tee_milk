package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.request.VerifyRequest;
import com.example.backend.dto.response.ResetPasswordRequest;
import com.example.backend.dto.response.ResetPasswordWithTokenRequest;
import com.example.backend.service.AuthService;
import com.example.backend.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> register (
            @RequestBody RegisterRequest registerRequest
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(authService.register(registerRequest),"register success"));
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<?>> verifyAccount (
            @RequestBody VerifyRequest verifyRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(authService.verifyAccount(verifyRequest),"verify success"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login (
            @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(authService.login(loginRequest),"login success"));
    }

    // 1. API Gửi yêu cầu
    @PostMapping("/forgot-password")
    public ResponseEntity<com.example.backend.common.BaseResponse> requestForgotPassword(
            @RequestBody com.example.backend.dto.request.ForgotPasswordRequest request) {
        String message = authService.requestForgotPassword(request);
        return ResponseEntity.ok(new com.example.backend.common.BaseResponse(200, null, message));
    }

    // 2. API Xác thực OTP (Bước 2 - Trả về Token nếu thành công)
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<com.example.backend.common.BaseResponse> verifyForgotPasswordOtp(
            @RequestBody com.example.backend.dto.request.ForgotPasswordVerifyRequest request) {

        String resetToken = authService.verifyForgotPasswordOtp(request);
        // Nhét resetToken vào phần data của BaseResponse để gửi về cho Android
        return ResponseEntity.ok(new com.example.backend.common.BaseResponse(200, resetToken, "Xác thực OTP thành công!"));
    }

    // 3. API Đổi mật khẩu (Bước 3 - Truyền Token lên để chốt hạ)
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<com.example.backend.common.BaseResponse> resetPasswordWithToken(
            @RequestBody ResetPasswordWithTokenRequest request) {

        String message = authService.resetPasswordWithToken(request);
        return ResponseEntity.ok(new com.example.backend.common.BaseResponse(200, null, message));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestHeader("Authorization") String token) {
        // @RequestHeader("Authorization") sẽ tự động bốc chuỗi "Bearer eyJhbGci..." từ Header của Postman/Mobile gửi lên
        String message = authService.logout(token);

        // Trả về chuỗi BaseResponse quen thuộc của Đốm
        return ResponseEntity.ok(new BaseResponse(200, null, message));
    }

}
