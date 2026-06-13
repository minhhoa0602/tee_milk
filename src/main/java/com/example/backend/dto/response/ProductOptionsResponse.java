package com.example.backend.dto.response;

import com.example.backend.entity.Size;
import com.example.backend.entity.Topping;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductOptionsResponse {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private BigDecimal basePrice;
    private List<Size> sizes;         // Danh sách Size (id, name, priceAdd)
    private List<String> sugarLevels;         // Chỉ trả về mảng chuỗi ["NONE", "LESS", "NORMAL"]
    private List<String> iceLevels;           // Chỉ trả về mảng chuỗi ["NONE", "LESS", "NORMAL"]
    private List<Topping> toppings;   // Danh sách Topping (id, name, price)
}
