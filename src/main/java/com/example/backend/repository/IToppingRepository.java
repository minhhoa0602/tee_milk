package com.example.backend.repository;

import com.example.backend.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IToppingRepository extends JpaRepository<Topping, Integer> {
}
