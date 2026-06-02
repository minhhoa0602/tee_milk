package com.example.backend.repository;

import com.example.backend.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISizeRepository extends JpaRepository<Size, Integer> {
}
