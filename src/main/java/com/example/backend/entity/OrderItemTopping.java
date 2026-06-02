package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item_toppings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemTopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "topping_name", nullable = false, length = 100)
    private String toppingName;

    @Column(name = "topping_price", nullable = false)
    private BigDecimal toppingPrice;
}
