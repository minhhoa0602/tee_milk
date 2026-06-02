package com.example.backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String receiverName;
    private String phoneNumber;
    private Integer addressId;
    private String payment;
    private String note;
    private List<Integer> selectedCartItemIds;
}
