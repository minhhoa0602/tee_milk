package com.example.backend.mapper;

import com.example.backend.dto.response.history.OrderItemResponse;
import com.example.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);
}
