package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, OrderStatus status);

    List<Order> findByUserId(Integer id);
}
