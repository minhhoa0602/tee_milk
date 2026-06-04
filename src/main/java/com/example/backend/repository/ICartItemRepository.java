package com.example.backend.repository;

import com.example.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserIdAndProductId(Integer id, Integer id1);

    List<CartItem> findByUserId(Integer id);
}
