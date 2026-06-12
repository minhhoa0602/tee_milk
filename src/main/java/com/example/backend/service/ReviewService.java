package com.example.backend.service;

import com.example.backend.dto.request.ReviewRequest;
import com.example.backend.entity.*;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.IOrderRepository;
import com.example.backend.repository.IReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final IReviewRepository reviewRepository;
    private final IOrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    public String createReview(User user, ReviewRequest request) {
        // 1. Kiểm tra đơn hàng có tồn tại không
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ApplicationException("Không tìm thấy đơn hàng này!", 404, 404));

        // 2. BẢO MẬT: Kiểm tra đơn hàng phải thuộc về đúng người đang đăng nhập
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ApplicationException("Bạn không có quyền đánh giá đơn hàng của người khác!", 403, 403);
        }

        // 3. LOGIC: Đơn hàng phải có trạng thái COMPLETED mới được đánh giá
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ApplicationException("Bạn chỉ có thể đánh giá những đơn hàng đã giao hoàn thành!", 400, 400);
        }

        // 4. LOGIC: Kiểm tra xem sản phẩm này có thực sự nằm trong đơn hàng đó không
        boolean isProductInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(request.getProductId()));

        if (!isProductInOrder) {
            throw new ApplicationException("Sản phẩm này không tồn tại trong đơn hàng của bạn!", 400, 400);
        }

        // 5. CHỐNG SPAM: Kiểm tra xem sản phẩm này trong đơn này đã được đánh giá trước đó chưa
        if (reviewRepository.existsByOrderIdAndProductId(request.getOrderId(), request.getProductId())) {
            throw new ApplicationException("Bạn đã đánh giá sản phẩm này rồi, không thể đánh giá lại!", 400, 400);
        }

        // Lấy thực thể Product ra từ danh sách chi tiết đơn để gán khóa ngoại
        Product product = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .map(OrderItem::getProduct)
                .findFirst()
                .get();

        // 6. Tạo thực thể Review mới và lưu xuống DB
        Review review = new Review();
        review.setUser(user);
        review.setOrder(order);
        review.setProduct(product);
        review.setRatingStar(request.getRatingStar());
        review.setComment(request.getComment() != null ? request.getComment().trim() : "");
        review.setImageUrl(request.getImageUrl());

        reviewRepository.save(review);

        return "Đánh giá sản phẩm thành công! Cảm ơn bạn đã phản hồi.";
    }
}
