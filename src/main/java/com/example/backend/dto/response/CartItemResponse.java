package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartItemResponse {

    private Integer cartItemId;
    private int productId;
    private String productName;
    private String productImage;
    private String productSize;
    private String iceLevel;
    private String sugarLevel;
    private List<String> toppingNames;
    private BigDecimal price;//don gia 1 ly = gia goc + tien size +topping
    private Integer quantity;
    private BigDecimal totalPrice;//gia*soluong
}
