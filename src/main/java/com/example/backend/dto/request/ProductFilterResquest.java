package com.example.backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterResquest {
    private String keyword;
    private Integer categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;
    private Integer ratingBucket;
}
