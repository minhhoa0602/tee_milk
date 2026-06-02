package com.example.backend.controller;

import com.example.backend.common.BaseResponse;
import com.example.backend.dto.request.ProductFilterResquest;
import com.example.backend.dto.response.ProductDetailResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.entity.Product;
import com.example.backend.service.IProductService;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
