package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findTop10ByIsActiveTrueOrderBySoldCount();

//    List<Product> findTop10ByIsActiveTrueOrderByCreatedAtDesc();
}
