package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Enumerated(EnumType.STRING)
    @Column(name = "ice_level", columnDefinition = "ENUM('NONE', 'LESS', 'NORMAL') DEFAULT 'NORMAL'")
    private LevelOption iceLevel = LevelOption.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "sugar_level", columnDefinition = "ENUM('NONE', 'LESS', 'NORMAL') DEFAULT 'NORMAL'")
    private LevelOption sugarLevel = LevelOption.NORMAL;

    private Integer quantity = 1;

    // Thiết lập mối quan hệ nhiều-nhiều với bảng Toppings thông qua bảng trung gian cart_item_toppings
    @ManyToMany
    @JoinTable(
            name = "cart_item_toppings",
            joinColumns = @JoinColumn(name = "cart_item_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private Set<Topping> toppings;
}
