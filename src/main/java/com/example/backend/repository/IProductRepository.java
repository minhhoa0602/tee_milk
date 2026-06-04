package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findTop10ByIsActiveTrueOrderBySoldCount();

//    List<Product> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

    // 1. Dùng cho khách lạ/Giỏ trống: Bốc ngẫu nhiên 10 món
    @Query(value = "SELECT * FROM products WHERE is_active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Product> findRandomActiveProducts(@Param("limit") int limit);

    // Lấy ngẫu nhiên sản phẩm theo Gu hương vị (Né các món đang có trong giỏ)
    @Query(value = "SELECT * FROM products WHERE is_active = true " +
            "AND flavor_profile = :flavor " +
            "AND id NOT IN :excludedIds " +
            "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Product> findByFlavorProfile(
            @Param("flavor") String flavor,
            @Param("excludedIds") List<Integer> excludedIds,
            @Param("limit") int limit);
}
