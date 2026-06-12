package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String imageUrl;
    private Integer soldCount;
    private Double averageRating;
    private List<ReviewResponse> reviews;
}
