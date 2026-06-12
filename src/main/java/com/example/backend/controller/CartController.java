package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.CartRequest;
import com.example.backend.dto.request.UpdateCartRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.User;
import com.example.backend.service.CartService;
import com.example.backend.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CartItemResponse>>> getCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(new BaseResponse<>(cartService.getCart(customUserDetails.getUser()), "successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponse> addToCart(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CartRequest cartRequest
            ){
        return ResponseEntity.ok(new BaseResponse<>(cartService.addToCart(customUserDetails.getUser(), cartRequest), "successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> removeFromCart(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Integer id
    ){
        return ResponseEntity.ok(new BaseResponse<>( cartService.removeFromCart(customUserDetails.getUser(), id), "successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateCartRequest request) {

        String result = cartService.updateCartItem(userDetails.getUser(), request);
        return ResponseEntity.ok(new BaseResponse(200, null, result));
    }
}
