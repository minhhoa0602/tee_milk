package com.example.backend.service;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {
    OrderResponse order (User user, OrderRequest orderRequest);
    List<OrderHistoryResponse> getOrderHistory (User user, String status);
    String reorder(User user, Integer oldOrderId);
    void handleWebhookPayment(Integer orderId, BigDecimal transferAmount);
    OrderResponse getOrderById(User user, Integer orderId);
}
