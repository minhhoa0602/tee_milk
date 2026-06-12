package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.config.CustomUserDetails;
import com.example.backend.dto.request.ProductFilterResquest;
import com.example.backend.dto.response.ProductDetailResponse;
import com.example.backend.dto.response.ProductOptionsResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.service.IProductService;
import com.example.backend.service.ProductService;
import com.example.backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final RecommendationService recommendationService;

    @GetMapping("/best-sellers")
    public ResponseEntity<BaseResponse<List<ProductResponse>>> getBestSellers() {
        return ResponseEntity.ok ( new BaseResponse<>(productService.getBestSellers(),"successfully"));
    }

//    @GetMapping("/new-arrivals")
//    public ResponseEntity<BaseResponse<List<ProductResponse>>> getNewArrivals() {
//        return null;
//    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<ProductResponse>>> searchProducts(ProductFilterResquest productFilterResquest) {
        return ResponseEntity.ok ( new BaseResponse<>(productService.searchProducts(productFilterResquest), "successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductDetailResponse>> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok ( new BaseResponse<>(productService.getProductDetail(id), "successfully"));
    }

    // API Gợi ý sản phẩm thông minh (Recommendation)
    // Đường dẫn: GET http://localhost:8080/api/products/recommendations
    @GetMapping("/recommendations")
    public ResponseEntity<BaseResponse> getRecommendations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Nếu không có Token gửi lên -> userDetails sẽ bị null
        User user = (userDetails != null) ? userDetails.getUser() : null;

        List<ProductResponse> data = recommendationService.getRecommendations(user);
        return ResponseEntity.ok(new BaseResponse(200, data, "Lấy danh sách gợi ý thành công"));
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<BaseResponse> getProductOptions(@PathVariable Integer id) {

        ProductOptionsResponse data = productService.getProductOptions(id);

        // Bọc vào BaseResponse quen thuộc để trả về cho Mobile
        return ResponseEntity.ok(new BaseResponse(200, data, "Lấy danh sách tùy chọn thành công"));
    }
}
