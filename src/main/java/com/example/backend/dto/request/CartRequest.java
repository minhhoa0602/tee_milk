package com.example.backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    private Integer productId;
    private Integer sizeId;
    private String iceLevel;
    private String sugarLevel;
    private List<Integer> toppingIds;//mang chua cac id topping khach chon
    private Integer quantity;
}
