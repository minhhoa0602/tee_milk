package com.example.backend.service;

import com.example.backend.dto.response.ProductResponse;
import com.example.backend.entity.*;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.mapper.ReviewMapper;
import com.example.backend.repository.ICartItemRepository;
import com.example.backend.repository.IOrderRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.IReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final IProductRepository productRepository;
    private final ProductMapper productMapper;
    private final IReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ICartItemRepository cartItemRepository;
    private final IOrderRepository orderRepository;

    public List<ProductResponse> getRecommendations(User user) {
        int LIMIT = 4;

        // 1. NẾU LÀ KHÁCH MỚI (Chưa đăng nhập) -> Trả về Random
        if (user == null) {
            return productMapper.productsToProductResponses(productRepository.findRandomActiveProducts(LIMIT));
        }

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        List<Order> pastOrders = orderRepository.findByUserId(user.getId());

        // 2. NẾU GIỎ TRỐNG VÀ CHƯA MUA GÌ -> Trả về Random
        if (cartItems.isEmpty() && pastOrders.isEmpty()) {
            return productMapper.productsToProductResponses(productRepository.findRandomActiveProducts(LIMIT));
        }

        // ==========================================
        // 3. BỘ NÃO PHÂN TÍCH KHẨU VỊ (TASTE PROFILING)
        // ==========================================
        int sugarScore = 0;   // Điểm đánh giá độ ngọt
        int toppingCount = 0; // Tổng số lượng topping đã từng gọi
        int totalDrinks = 0;  // Tổng số ly nước đã quét
        Set<Integer> excludedProductIds = new HashSet<>();

        // Quét giỏ hàng hiện tại
        for (CartItem item : cartItems) {
            excludedProductIds.add(item.getProduct().getId());
            totalDrinks += item.getQuantity();
            toppingCount += (item.getToppings().size() * item.getQuantity());

            if ("NORMAL".equals(item.getSugarLevel().name())) sugarScore += (1 * item.getQuantity());
            else sugarScore -= (1 * item.getQuantity()); // LESS hoặc NONE bị trừ điểm ngọt
        }

        // Quét lịch sử đã mua
        for (Order order : pastOrders) {
            for (OrderItem item : order.getOrderItems()) {
                totalDrinks += item.getQuantity();
                // Bảng order_item_toppings của bạn lưu snapshot
                toppingCount += (item.getOrderItemToppings().size() * item.getQuantity());

                if ("NORMAL".equals(item.getSugarLevel())) sugarScore += (1 * item.getQuantity());
                else sugarScore -= (1 * item.getQuantity());
            }
        }

        // 4. CHỐT HẠ GU KHÁCH HÀNG
        String userGu = "BALANCED"; // Mặc định là cân bằng

        // Nếu điểm đường âm (toàn uống ít đường) VÀ trung bình mỗi ly có ít hơn 1 topping
        if (sugarScore < 0 && (totalDrinks > 0 && (toppingCount / totalDrinks) < 1)) {
            userGu = "REFRESHING"; // Khách hàng A: Gu thanh mát, ít ngọt
        }
        // Nếu điểm đường dương (khoái ăn ngọt) HOẶC trung bình mỗi ly táng từ 1.5 topping trở lên
        else if (sugarScore > 0 || (totalDrinks > 0 && (toppingCount / totalDrinks) >= 1)) {
            userGu = "RICH";       // Khách hàng B: Gu béo ngậy, nghiện topping
        }

        // 5. TRUY XUẤT DATABASE THEO ĐÚNG GU
        if (excludedProductIds.isEmpty()) excludedProductIds.add(-1); // Fix lỗi SQL rỗng

        List<Product> recommendedProducts = productRepository.findByFlavorProfile(userGu, excludedProductIds.stream().toList(), LIMIT);

        // 6. AUTO-FILL: Nếu list trà trái cây (REFRESHING) trong quán không đủ 10 món, bốc đại các món BALANCED đắp vào cho đủ
        if (recommendedProducts.size() < LIMIT) {
            int missingCount = LIMIT - recommendedProducts.size();
            List<Product> fillers = productRepository.findRandomActiveProducts(LIMIT);
            for (Product filler : fillers) {
                if (recommendedProducts.size() >= LIMIT) break;
                if (!excludedProductIds.contains(filler.getId()) && !recommendedProducts.contains(filler)) {
                    recommendedProducts.add(filler);
                }
            }
        }

        return productMapper.productsToProductResponses(recommendedProducts);
    }
}
