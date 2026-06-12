package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserProfileResponse;
import com.example.backend.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // 1. API Lấy thông tin Profile (Khi vừa mở màn hình Profile cá nhân)
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse data = userService.getProfile(userDetails.getUser());
        return ResponseEntity.ok(new BaseResponse(200, data, "Lấy thông tin cá nhân thành công"));
    }

    // 2. API Chỉnh sửa thông tin Profile (Khi ấn nút Lưu / Cập nhật)
    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {

        UserProfileResponse data = userService.updateProfile(userDetails.getUser(), request);
        return ResponseEntity.ok(new BaseResponse(200, data, "Cập nhật thông tin cá nhân thành công"));
    }
}