package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {
    private Integer orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String paymentStatus;
    private String qrCodeUrl;
}
