package com.example.backend.service;

import com.example.backend.dto.request.ProductFilterResquest;
import com.example.backend.dto.response.ProductDetailResponse;
import com.example.backend.dto.response.ProductOptionsResponse;
import com.example.backend.dto.response.ProductResponse;
import com.example.backend.dto.response.ReviewResponse;
import com.example.backend.entity.Product;
import com.example.backend.entity.Review;
import com.example.backend.entity.Size;
import com.example.backend.entity.Topping;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.exception.ApplicationException;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.mapper.ReviewMapper;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IReviewRepository;
import com.example.backend.repository.ISizeRepository;
import com.example.backend.repository.IToppingRepository;
import com.example.backend.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final ProductMapper productMapper;
    private final IReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ISizeRepository sizeRepository;
    private final IToppingRepository toppingRepository;

    @Override
    public List<ProductResponse> getBestSellers() {
        List<Product> productList = productRepository.findTop10ByIsActiveTrueOrderBySoldCount();
        return productMapper.productsToProductResponses(productList);
    }

    @Override
    public List<ProductResponse> searchProducts(ProductFilterResquest productFilterResquest) {
        String keyword = productFilterResquest.getKeyword();
        Integer categoryId = productFilterResquest.getCategoryId();
        BigDecimal minPrice = productFilterResquest.getMinPrice();
        BigDecimal maxPrice = productFilterResquest.getMaxPrice();
        String sortBy = productFilterResquest.getSortBy();
        Integer ratingBucket = productFilterResquest.getRatingBucket();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if("price_asc".equalsIgnoreCase(sortBy)){
            sort = Sort.by(Sort.Direction.ASC, "basePrice");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "basePrice");
        }

        Specification<Product> specification = Specification.allOf();

        specification = specification.and(ProductSpecification.productActive(true));
        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(ProductSpecification.productName(keyword));
        }
        if (categoryId != null) {
            specification = specification.and(ProductSpecification.categoryId(categoryId));
        }
        if (minPrice != null) {
            specification = specification.and(ProductSpecification.minPrice(minPrice));
        }
        if (maxPrice != null) {
            specification = specification.and(ProductSpecification.maxPrice(maxPrice));
        }
        if (ratingBucket != null) {
            specification = specification.and(ProductSpecification.productRatingRange(ratingBucket));
        }

        List<Product> productList = productRepository.findAll(specification, sort);

        return productMapper.productsToProductResponses(productList);
    }

    @Override
    public ProductDetailResponse  getProductDetail(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ApplicationErrors.PRODUCT_NOT_FOUND);
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(id);
        List<ReviewResponse> reviewResponses = reviewMapper.reviewsToReviewResponseList(reviews);
        double avgRating = reviews.stream()
                .mapToInt(Review :: getRatingStar)
                .average()
                .orElse(0.0);
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        productDetailResponse.setId(product.getId());
        productDetailResponse.setName(product.getName());
        productDetailResponse.setDescription(product.getDescription());
        productDetailResponse.setBasePrice(product.getBasePrice());
        productDetailResponse.setSoldCount(product.getSoldCount());
        productDetailResponse.setImageUrl(product.getImageUrl());
        productDetailResponse.setAverageRating(Math.round(avgRating * 10) / 10.0 );
        productDetailResponse.setReviews(reviewResponses);
        return productDetailResponse;
    }

    @Override
    public ProductOptionsResponse getProductOptions(Integer productId) {
        // 1. Kiểm tra sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new com.example.backend.exception.ApplicationException("Sản phẩm không tồn tại!", 404, 404));

        // 2. Lấy thẳng danh sách Entity từ Database lên (Không cần map sang DTO nữa)
        List<Size> sizes = sizeRepository.findAll();
        List<Topping> toppings = toppingRepository.findAll();

        // 3. Khởi tạo mảng String cho Đường và Đá
        List<String> sugarLevels = List.of("NONE", "LESS", "NORMAL");
        List<String> iceLevels = List.of("NONE", "LESS", "NORMAL");

        // 4. Đóng gói và trả về
        return ProductOptionsResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sizes(sizes)
                .sugarLevels(sugarLevels)
                .iceLevels(iceLevels)
                .toppings(toppings)
                .build();
    }
}
