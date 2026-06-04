package com.example.backend.service;

import com.example.backend.common.BaseResponse;
import com.example.backend.dto.request.ProductFilterResquest;
import com.example.backend.dto.response.ProductDetailResponse;
import com.example.backend.dto.response.ProductOptionsResponse;
import com.example.backend.dto.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getBestSellers();
//    List<ProductResponse> getNewArrivals();
    List<ProductResponse> searchProducts(ProductFilterResquest productFilterResquest);
    ProductDetailResponse  getProductDetail(Integer id);

    ProductOptionsResponse getProductOptions(Integer productId);
}
