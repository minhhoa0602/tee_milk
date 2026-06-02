package com.example.backend.repository;

import com.example.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICartRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(Integer id);

    List<CartItem> findByUserIdAndProductId(Integer id, Integer id1);

    List<CartItem> findByIdInAndUserId(List<Integer> selectedCartItemIds, Integer id);
}
