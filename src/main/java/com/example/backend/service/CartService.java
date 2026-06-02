package com.example.backend.service;

import com.example.backend.dto.request.CartRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.mapper.CartMapper;
import com.example.backend.repository.ICartRepository;
import com.example.backend.repository.IProductRepository;
import com.example.backend.repository.ISizeRepository;
import com.example.backend.repository.IToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final ICartRepository cartRepository;
    private final ISizeRepository sizeRepository;
    private final IToppingRepository toppingRepository;
    private final IProductRepository productRepository;
    private final CartMapper cartMapper;

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        BigDecimal toppingPrice = cartItem.getToppings()
                .stream()
                .map(Topping::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal price = cartItem.getProduct().getBasePrice()
                .add(cartItem.getSize().getPriceAdd())
                .add(toppingPrice);
        CartItemResponse cartItemResponse = cartMapper.cartItemtoCartItemResponse(cartItem);
        cartItemResponse.setPrice(price);
        cartItemResponse.setTotalPrice(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        return cartItemResponse;
    }

    @Override
    public List<CartItemResponse> getCart(User user) {

        List<CartItem> cartItems = cartRepository.findByUserId(user.getId());
        for (CartItem item : cartItems) {
            System.out.println(item.getToppings());
        }
        return cartItems.stream()
                .map(this::convertToCartItemResponse)
                .toList();
    }

    @Override
    public String addToCart(User user, CartRequest cartRequest) {
        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> ApplicationErrors.PRODUCT_NOT_FOUND);
        Size size = sizeRepository.findById(cartRequest.getSizeId())
                .orElseThrow(() -> ApplicationErrors.SIZE_NOT_FOUND);
        Set<Topping> toppingSet = new HashSet<>();
        if (cartRequest.getToppingIds() != null && !cartRequest.getToppingIds().isEmpty()) {
            toppingSet.addAll(toppingRepository.findAllById(cartRequest.getToppingIds()));
        }
        LevelOption iceOption = LevelOption.valueOf(cartRequest.getIceLevel());
        LevelOption sugarOption = LevelOption.valueOf(cartRequest.getSugarLevel());
        //ktra xem gio hang co mon nao y het ko
        List<CartItem> existingCartItems = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        Optional<CartItem> matchedItem = existingCartItems.stream()
                .filter(cartItem -> cartItem.getSize().getId().equals(size.getId())
                && cartItem.getIceLevel() == iceOption
                && cartItem.getSugarLevel() == sugarOption
                && cartItem.getToppings().size() == toppingSet.size()
                && cartItem.getToppings().containsAll(toppingSet))
                .findFirst();
        if (matchedItem.isPresent()) {
            //neu co mon y het -> cong so luong
            CartItem cartItem = matchedItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartRepository.save(cartItem);
        }else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setSize(size);
            newCartItem.setIceLevel(iceOption);
            newCartItem.setSugarLevel(sugarOption);
            newCartItem.setToppings(toppingSet);
            newCartItem.setQuantity(cartRequest.getQuantity());
            cartRepository.save(newCartItem);
        }
        return "Đã thêm vào giỏ hàng thành công!";
    }

    @Override
    public String removeFromCart(User user, Integer id) {
        CartItem cartItem = cartRepository.findById(id)
                .orElseThrow(() -> ApplicationErrors.CART_NOT_FOUND);
        // chi cho phep xoa mon cua chinh user do
        if (!cartItem.getUser().getId().equals(user.getId())) {
             throw ApplicationErrors.ERRORS_USER;
        }
        cartRepository.delete(cartItem);
        return "Đã xóa món khỏi giỏ hàng.";
    }


}
