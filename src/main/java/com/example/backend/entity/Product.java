package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice; // Dùng BigDecimal cho tiền tệ là chuẩn xác nhất

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sold_count")
    private Integer soldCount;

    @Column(name = "is_active")
    private Boolean isActive;

    // Quan hệ N-1: Nhiều Sản phẩm thuộc 1 Danh mục
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
