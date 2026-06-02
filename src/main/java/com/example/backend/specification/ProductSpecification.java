package com.example.backend.specification;

import com.example.backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> productActive(Boolean isActive) {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }
    public static Specification<Product> productName(String keyword) {
        return (root, query, cb) -> cb.like(root.get("name"), "%" + keyword.toLowerCase() + "%");
    }
    public static Specification<Product> categoryId (Integer categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }
    public static Specification<Product> minPrice (BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
    }
    public static Specification<Product> maxPrice (BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }
}
