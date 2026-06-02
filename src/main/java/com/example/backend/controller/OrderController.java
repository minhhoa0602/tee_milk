package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.CartRequest;
import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.User;
import com.example.backend.service.IOrderService;
import com.example.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponse>> order(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody OrderRequest orderRequest
    ){
        return ResponseEntity.ok(new BaseResponse<>(orderService.order(customUserDetails.getUser(), orderRequest), "successfully"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderHistoryResponse>>> getOrderHistory(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam( required = false) String status
    ){
        return ResponseEntity.ok(new BaseResponse<>(orderService.getOrderHistory(customUserDetails.getUser(), status), "successfully"));
    }
}
