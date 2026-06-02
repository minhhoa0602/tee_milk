package com.example.backend.dto.response.history;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private String productName;
    private int quantity;
    private String productImageUrl;
    private BigDecimal unitPrice;
}
