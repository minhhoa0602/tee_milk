package com.example.backend.dto.response;

import com.example.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer id;
    private String name;
    private BigDecimal basePrice;
    private String imageUrl;
    private Integer soldCount;
    private String categoryName;
}
