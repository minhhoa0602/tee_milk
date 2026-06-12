package com.example.backend.service;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.exception.ApplicationException;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final ICartRepository cartRepository;
    private final IAddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final ICartItemRepository cartItemRepository;
    private final ISizeRepository sizeRepository;
    private final IToppingRepository toppingRepository;

    @Override
    @Transactional
    public OrderResponse order(User user, OrderRequest orderRequest) {
        // 1. Kiểm tra giỏ hàng trống
        if (orderRequest.getSelectedCartItemIds() == null || orderRequest.getSelectedCartItemIds().isEmpty()) {
            throw ApplicationErrors.ODER_EMPTY;
        }

        List<CartItem> selectedItems = cartRepository.findByIdInAndUserId(orderRequest.getSelectedCartItemIds(), user.getId());
        if (selectedItems == null || selectedItems.isEmpty()) {
            throw ApplicationErrors.ODER_ERROR;
        }

        // 2. Kiểm tra địa chỉ giao hàng
        Address address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> ApplicationErrors.ADDRESS_NOT_FOUND);
        if (!address.getUser().getId().equals(user.getId())) {
            throw ApplicationErrors.ADDRESS_ERROR;
        }

        if (orderRequest.getReceiverName() == null || orderRequest.getPhoneNumber() == null) {
            throw ApplicationErrors.NAMEORPHONE_ERROR;
        }

        // 3. Khởi tạo đơn hàng (Tạm thời lưu tổng tiền bằng 0 để lấy ID trước)
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(PaymentMethod.valueOf(orderRequest.getPayment()));
        order.setStatus(OrderStatus.PENDING);
        order.setReceiverName(orderRequest.getReceiverName());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setNote(orderRequest.getNote());
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order); // Lưu trước để sinh ID đơn hàng

        // 4. Vòng lặp tối ưu duy nhất: Tính tiền + Tạo OrderItem + Tăng số lượng đã bán
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : selectedItems) {
            // Tính đơn giá cho từng ly trà sữa (Base Price + Size Price + Toppings)
            BigDecimal toppingTotals = cartItem.getToppings().stream()
                    .map(Topping::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal price = cartItem.getProduct().getBasePrice()
                    .add(cartItem.getSize().getPriceAdd())
                    .add(toppingTotals);

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            // Map và lưu chi tiết món trong đơn hàng
            OrderItem orderItem = orderMapper.cartItemToOrderItem(cartItem);
            orderItem.setOrder(order);
            orderItem.setUnitPrice(price);
            orderItemRepository.save(orderItem);

            // CẬP NHẬT TRƯỜNG soldCount CÓ SẴN CỦA ĐỐM
            Product product = cartItem.getProduct();
            int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;

            // Cộng dồn số lượng ly khách vừa mua vào cột đã bán
            product.setSoldCount(currentSold + cartItem.getQuantity());
        }

        // 5. Cập nhật tổng tiền chính xác cuối cùng cho Đơn hàng
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // Xóa sạch các món đã thanh toán khỏi giỏ hàng
        cartRepository.deleteAll(selectedItems);

        // 6. XỬ LÝ SINH MÃ QR MOMO THEO SỐ TIỀN THẬT
        String qr = null;
        if ("MOMO".equals(orderRequest.getPayment())) {
            String momoPhone = "0378933396";
            String momoLink = "https://nhantien.momo.vn/" + momoPhone + "/" + totalAmount.toBigInteger();
            qr = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=" + URLEncoder.encode(momoLink, StandardCharsets.UTF_8);
        }

        return orderMapper.orderToOrderResponse(order, totalAmount, qr);
    }

    @Override
    public List<OrderHistoryResponse> getOrderHistory(User user, String status) {
        List<Order> orders;
        if (status == null || status.trim().isEmpty() || status.equals("ALL")) {
            orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        }else {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), orderStatus);
        }
        return orderMapper.orderToOrderHistoryResponses(orders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String reorder(User user, Integer oldOrderId) {
        // 1. Tìm đơn hàng cũ của khách
        Order oldOrder = orderRepository.findById(oldOrderId)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy đơn hàng cũ này!", 404, 404));

        // Bảo mật: Đảm bảo khách không thể "đặt lại" đơn hàng của người khác
        if (!oldOrder.getUser().getId().equals(user.getId())) {
            throw new ApplicationException("Bạn không có quyền đặt lại đơn hàng này!", 403, 403);
        }

        // 2. Lấy toàn bộ giỏ hàng hiện tại của user để phục vụ logic check trùng/gộp món
        List<CartItem> currentCart = cartItemRepository.findByUserId(user.getId());

        // 3. Lặp qua từng món trong đơn hàng cũ để xử lý đưa vào giỏ
        for (OrderItem oldItem : oldOrder.getOrderItems()) {
            Product product = oldItem.getProduct();

            // Kiểm tra xem sản phẩm gốc hiện tại còn bán không
            if (product == null || Boolean.FALSE.equals(product.getIsActive())) {
                throw new ApplicationException("Sản phẩm '" + oldItem.getProductName() + "' hiện đã ngừng kinh doanh!", 400, 400);
            }

            // Tìm thực thể Size hiện tại từ text snapshot cũ
            Size size = sizeRepository.findByName(oldItem.getSizeName())
                    .orElseThrow(() -> new ApplicationException("Kích cỡ '" + oldItem.getSizeName() + "' không còn tồn tại!", 400, 400));

            // Tìm danh sách Topping thực thể hiện tại từ text snapshot cũ
            List<String> oldToppingNames = oldItem.getOrderItemToppings().stream()
                    .map(OrderItemTopping::getToppingName)
                    .toList();

            Set<Topping> toppings = new HashSet<>(toppingRepository.findByNameIn(oldToppingNames));

            // Đọc mức đường, đá cũ
            LevelOption iceLevel = LevelOption.valueOf(oldItem.getIceLevel());
            LevelOption sugarLevel = LevelOption.valueOf(oldItem.getSugarLevel());

            // 4. KIỂM TRA TRÙNG LẶP: Xem món cũ này đã có sẵn trong giỏ hàng hiện tại chưa
            Optional<CartItem> duplicateItem = currentCart.stream()
                    .filter(cart -> cart.getProduct().getId().equals(product.getId())
                            && cart.getSize().getId().equals(size.getId())
                            && cart.getIceLevel() == iceLevel
                            && cart.getSugarLevel() == sugarLevel
                            && cart.getToppings().size() == toppings.size()
                            && cart.getToppings().containsAll(toppings))
                    .findFirst();

            if (duplicateItem.isPresent()) {
                // Nếu đã có sẵn món y hệt trong giỏ -> Cộng dồn số lượng cũ vào
                CartItem existingCartItem = duplicateItem.get();
                existingCartItem.setQuantity(existingCartItem.getQuantity() + oldItem.getQuantity());
                cartItemRepository.save(existingCartItem);
            } else {
                // Nếu chưa có -> Tạo một item giỏ hàng mới tinh
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setProduct(product);
                newCartItem.setSize(size);
                newCartItem.setIceLevel(iceLevel);
                newCartItem.setSugarLevel(sugarLevel);
                newCartItem.setQuantity(oldItem.getQuantity());
                newCartItem.setToppings(toppings);

                cartItemRepository.save(newCartItem);
            }
        }

        return "Đã thêm toàn bộ các món từ đơn hàng cũ vào giỏ hàng của bạn!";
    }
}
