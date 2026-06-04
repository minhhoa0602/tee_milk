package com.example.backend.specification;

import com.example.backend.entity.Product;
import com.example.backend.entity.Review;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> productActive(Boolean isActive) {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }
    public static Specification<Product> productName(String keyword) {
        return (root, query, cb) -> cb.like(root.get("name"), "%" + keyword.toLowerCase() + "%");
    }
    public static Specification<Product> categoryId (Integer categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }
    public static Specification<Product> minPrice (BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
    }
    public static Specification<Product> maxPrice (BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }
    public static Specification<Product> productRatingRange(Integer bucket) {
        return (root, query, cb) -> {
            // 1. Tạo câu truy vấn con tính điểm AVG của sản phẩm
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<Review> reviewRoot = subquery.from(Review.class);
            subquery.select(cb.avg(reviewRoot.get("ratingStar")));
            subquery.where(cb.equal(reviewRoot.get("product").get("id"), root.get("id")));

            // Chuyển đổi Subquery thành biểu thức giá trị, nếu món chưa ai vote thì coi như 0.0 sao
            jakarta.persistence.criteria.Expression<Double> avgRating = cb.coalesce(subquery, 0.0);

            // 2. Tính toán biên độ dựa theo số nút khách bấm
            double min = bucket.doubleValue(); // Ví dụ bấm nút 2 -> min = 2.0
            double max = min + 1.0;            // max = 3.0

            // 3. Xử lý phân tách toán tử gối đầu chống trùng lặp dữ liệu
            if (bucket == 5) {
                // Nếu bấm nút 5 sao: Chỉ lấy những món có điểm trung bình bằng đúng 5.0
                return cb.equal(avgRating, 5.0);
            }

            // Nếu bấm các nút từ 1 đến 4: Lấy khoảng [min, max)
            // Ví dụ bấm nút 2: Lấy các món có điểm từ 2.0 đến dưới 3.0 (2.99 sao vẫn nhận)
            return cb.and(
                    cb.greaterThanOrEqualTo(avgRating, min),
                    cb.lessThan(avgRating, max)
            );
        };
    }
}
