package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.ReviewRequest;
import com.example.backend.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    // API Đăng bài viết đánh giá sản phẩm
    // Đường dẫn: POST http://localhost:8080/api/reviews
    @PostMapping
    public ResponseEntity<BaseResponse> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {

        String result = reviewService.createReview(userDetails.getUser(), request);
        return ResponseEntity.ok(new BaseResponse(200, null, result));
    }
}
