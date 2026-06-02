package com.example.backend.service;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.User;

import java.util.List;

public interface IOrderService {
    OrderResponse order (User user, OrderRequest orderRequest);
    List<OrderHistoryResponse> getOrderHistory (User user, String status);
}
