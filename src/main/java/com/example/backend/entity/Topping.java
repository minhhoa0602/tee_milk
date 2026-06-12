package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "toppings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal price = BigDecimal.ZERO;
}