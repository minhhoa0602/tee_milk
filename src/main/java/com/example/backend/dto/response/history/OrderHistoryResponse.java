package com.example.backend.dto.response.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderHistoryResponse {
    private String orderCode;

    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDate orderDate;
    private String orderStatus;
    private BigDecimal totalAmount;

    private List<OrderItemResponse> orderItems;

}
