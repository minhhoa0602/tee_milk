package com.example.backend.repository;

import com.example.backend.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IToppingRepository extends JpaRepository<Topping, Integer> {
    List<Topping> findByNameIn(List<String> oldToppingNames);
}
