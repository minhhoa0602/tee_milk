package com.example.backend.service;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.repository.IAddressRepository;
import com.example.backend.repository.ICartRepository;
import com.example.backend.repository.IOrderItemRepository;
import com.example.backend.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final ICartRepository cartRepository;
    private final IAddressRepository addressRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse order(User user, OrderRequest orderRequest) {
        if (orderRequest.getSelectedCartItemIds() == null || orderRequest.getSelectedCartItemIds().isEmpty()) {
            throw ApplicationErrors.ODER_EMPTY;
        }

        List<CartItem> selectedItems = cartRepository.findByIdInAndUserId(orderRequest.getSelectedCartItemIds(),user.getId());
        if (selectedItems == null || selectedItems.isEmpty()) {
            throw ApplicationErrors.ODER_ERROR;
        }

        Address address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> ApplicationErrors.ADDRESS_NOT_FOUND);
        if (!address.getUser().getId().equals(user.getId())) {
            throw ApplicationErrors.ADDRESS_ERROR;
        }

        if (orderRequest.getReceiverName() == null || orderRequest.getPhoneNumber() == null) {
            throw ApplicationErrors.NAMEORPHONE_ERROR;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(PaymentMethod.valueOf(orderRequest.getPayment()));
        order.setStatus(OrderStatus.PENDING);
        order.setReceiverName(orderRequest.getReceiverName());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setNote(orderRequest.getNote());
        order=orderRepository.save(order);

        for (CartItem cartItem : selectedItems) {
            BigDecimal toppingTotals  = cartItem.getToppings().stream()
                    .map(Topping::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal price = cartItem.getProduct().getBasePrice()
                    .add(cartItem.getSize().getPriceAdd())
                    .add(toppingTotals);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = orderMapper.cartItemToOrderItem(cartItem);
            orderItem.setOrder(order);
            orderItem.setUnitPrice(price);
            orderItemRepository.save(orderItem);
        }
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        cartRepository.deleteAll(selectedItems);

        // 6. XỬ LÝ SINH MÃ QR MOMO (NẾU CÓ)
        String qr = null;

        if ("MOMO".equals(orderRequest.getPayment())) {
            String momoPhone = "0378933396";
            String momoLink = "https://nhantien.momo.vn/" + momoPhone + "/"+totalAmount.toBigInteger();
            qr = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data="+ URLEncoder.encode(momoLink, StandardCharsets.UTF_8);
        }
        return orderMapper.orderToOrderResponse(order,totalAmount,qr);
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
}
