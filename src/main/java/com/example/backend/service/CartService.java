package com.example.backend.service;

import com.example.backend.dto.request.CartRequest;
import com.example.backend.dto.request.UpdateCartRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.ApplicationErrors;
import com.example.backend.exception.ApplicationException;
import com.example.backend.mapper.CartMapper;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ICartItemRepository cartItemRepository;
    private final CartMapper cartMapper;


    @Override
    public List<CartItemResponse> getCart(User user) {

        List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

        return cartItems.stream()
                .map(cartMapper::cartItemtoCartItemResponse)
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
        // Trường hợp 1: Không truyền ID -> Xóa toàn bộ giỏ hàng của User này
        if (id == null) {
            // Lưu ý: Bạn cần có hàm deleteByUser trong CartRepository nhé
            cartRepository.deleteByUser(user);
            return "Đã xóa toàn bộ sản phẩm khỏi giỏ hàng.";
        }

        // Trường hợp 2: Có truyền ID -> Chỉ xóa món đó
        CartItem cartItem = cartRepository.findById(id)
                .orElseThrow(() -> ApplicationErrors.CART_NOT_FOUND);

        // Chỉ cho phép xóa món của chính user đó
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw ApplicationErrors.ERRORS_USER;
        }

        cartRepository.delete(cartItem);
        return "Đã xóa món khỏi giỏ hàng.";
    }

    @Override
    @Transactional
    public String updateCartItem(User user, UpdateCartRequest request) {
        // 1. Tìm dòng giỏ hàng cần chỉnh sửa
        CartItem currentItem = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new ApplicationException("Không tìm thấy món hàng trong giỏ!", 404, 404));

        // Bảo mật: Kiểm tra xem món trong giỏ này có đúng là của User đang đăng nhập không
        if (!currentItem.getUser().getId().equals(user.getId())) {
            throw new ApplicationException("Bạn không có quyền thao tác trên giỏ hàng này!", 403, 403);
        }

        // 2. Bốc các thực thể mới dựa theo request gửi lên
        Size newSize = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new ApplicationException("Kích cỡ không tồn tại!", 400, 400));

        LevelOption newIce = LevelOption.valueOf(request.getIceLevel());
        LevelOption newSugar = LevelOption.valueOf(request.getSugarLevel());

        java.util.Set<Topping> newToppings = new java.util.HashSet<>();
        if (request.getToppingIds() != null && !request.getToppingIds().isEmpty()) {
            newToppings.addAll(toppingRepository.findAllById(request.getToppingIds()));
        }

        // 3. KIỂM TRA XEM CÓ BỊ TRÙNG VỚI DÒNG KHÁC KHÔNG (Trừ chính nó ra)
        // Lấy tất cả các item có cùng Product của User này trong giỏ
        List<CartItem> userCartItems = cartItemRepository.findByUserIdAndProductId(user.getId(), currentItem.getProduct().getId());

        java.util.Optional<CartItem> matchedItem = userCartItems.stream()
                .filter(item -> !item.getId().equals(currentItem.getId()) // Loại trừ chính dòng đang sửa
                        && item.getSize().getId().equals(newSize.getId())
                        && item.getIceLevel() == newIce
                        && item.getSugarLevel() == newSugar
                        && item.getToppings().size() == newToppings.size()
                        && item.getToppings().containsAll(newToppings))
                .findFirst();

        if (matchedItem.isPresent()) {
            // NẾU TRÙNG: Cộng dồn số lượng mới vào dòng đã có sẵn kia
            CartItem existingItem = matchedItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);

            // Xóa bỏ hoàn toàn dòng cũ đang sửa đi (Vì đã gộp sang dòng kia rồi)
            cartItemRepository.delete(currentItem);
            return "Đã gộp trùng món và cập nhật giỏ hàng!";
        } else {
            // NẾU KHÔNG TRÙNG: Tiến hành ghi đè thông tin mới lên chính dòng này
            currentItem.setSize(newSize);
            currentItem.setIceLevel(newIce);
            currentItem.setSugarLevel(newSugar);
            currentItem.setToppings(newToppings);
            currentItem.setQuantity(request.getQuantity()); // Ghi đè số lượng mới hoàn toàn

            cartItemRepository.save(currentItem);
            return "Cập nhật giỏ hàng thành công!";
        }
    }

}
