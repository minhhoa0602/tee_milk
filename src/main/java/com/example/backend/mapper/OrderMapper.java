package com.example.backend.mapper;

import com.example.backend.dto.response.OrderResponse;
import com.example.backend.dto.response.history.OrderHistoryResponse;
import com.example.backend.entity.CartItem;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sizeName", source = "size.name")
    OrderItem cartItemToOrderItem(CartItem cartItem);

    @Mapping(target = "paymentMethod", expression = "java(order.getPaymentMethod().name())")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "qrCodeUrl", source = "qrCodeUrl")
    OrderResponse orderToOrderResponse(Order order, BigDecimal totalAmount, String qrCodeUrl);

    @Mapping(target = "orderCode", expression = "java(\"#MT\" + String.format(\"%08d\", order.getId()))")
    @Mapping(target = "orderStatus", expression = "java(order.getStatus().name())")
    OrderHistoryResponse orderToOrderHistoryResponse(Order order);

    List<OrderHistoryResponse> orderToOrderHistoryResponses (List<Order> orders);

}
