package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.request.VerifyRequest;
import com.example.backend.service.AuthService;
import com.example.backend.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
