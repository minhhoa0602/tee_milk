package com.example.backend.mapper;

import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.imageUrl")
    @Mapping(target = "productSize", source = "size.name")
    @Mapping(target = "iceLevel", expression = "java(cartItem.getIceLevel().name())")
    @Mapping(target = "sugarLevel", expression = "java(cartItem.getSugarLevel().name())")
    @Mapping(target = "toppingNames", expression = "java(getToppingNames(cartItem))")
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CartItemResponse cartItemtoCartItemResponse(CartItem cartItem);

    default List<String> getToppingNames(CartItem cartItem){
        return cartItem.getToppings()
                .stream()
                .map(topping -> topping.getName())
                .collect(Collectors.toList());
    }
}
