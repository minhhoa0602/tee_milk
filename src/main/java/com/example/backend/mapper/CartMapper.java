package com.example.backend.mapper;

import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.CartItem;
import com.example.backend.entity.Topping;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.imageUrl")
    @Mapping(target = "productSize", source = "size.name")
    @Mapping(target = "iceLevel", expression = "java(cartItem.getIceLevel().name())")
    @Mapping(target = "sugarLevel", expression = "java(cartItem.getSugarLevel().name())")
    @Mapping(target = "toppingNames", expression = "java(getToppingNames(cartItem))")
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CartItemResponse cartItemtoCartItemResponse(CartItem cartItem);

    default List<String> getToppingNames(CartItem cartItem) {
        return cartItem.getToppings()
                .stream()
                .map(Topping::getName)
                .toList();
    }

    @AfterMapping
    default void setCalculatedFields(
            CartItem cartItem,
            @MappingTarget  CartItemResponse.CartItemResponseBuilder response) {
        System.out.println("AFTER MAPPING RUNNING");
        BigDecimal toppingPrice = cartItem.getToppings()
                .stream()
                .map(Topping::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal price = cartItem.getProduct().getBasePrice()
                .add(cartItem.getSize().getPriceAdd())
                .add(toppingPrice);

        response.price(price);
        response.totalPrice(
                price.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
        );
    }
}