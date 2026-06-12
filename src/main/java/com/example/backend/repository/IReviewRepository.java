package com.example.backend.repository;

import com.example.backend.entity.Review;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Integer id);

    boolean existsByOrderIdAndProductId(@NotNull(message = "Mã đơn hàng không được để trống") Integer orderId, @NotNull(message = "Mã sản phẩm không được để trống") Integer productId);
}
