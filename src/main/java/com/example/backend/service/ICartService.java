package com.example.backend.service;

import com.example.backend.dto.request.CartRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.User;

import java.util.List;

public interface ICartService {
    List<CartItemResponse> getCart(User user);
    String addToCart(User user, CartRequest cartRequest);
    String removeFromCart(User user, Integer id);
}
