package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"order_id", "product_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "rating_star", nullable = false)
    private Integer ratingStar;

    private String comment;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
