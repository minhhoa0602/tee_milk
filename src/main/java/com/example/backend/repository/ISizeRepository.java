package com.example.backend.repository;

import com.example.backend.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISizeRepository extends JpaRepository<Size, Integer> {
    Optional<Size> findByName(String sizeName);
}
