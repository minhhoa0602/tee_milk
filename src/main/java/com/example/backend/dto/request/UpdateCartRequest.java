package com.example.backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCartRequest {
    private Integer cartItemId;  // ID của dòng giỏ hàng cần sửa
    private Integer sizeId;      // Size mới (hoặc giữ nguyên cũ)
    private String iceLevel;     // Mức đá mới
    private String sugarLevel;   // Mức đường mới
    private List<Integer> toppingIds; // Danh sách Topping mới sau khi chỉnh sửa
    private Integer quantity;    // Số lượng mới hoàn toàn sau khi bấm +/- hoặc chọn ở popup
}
