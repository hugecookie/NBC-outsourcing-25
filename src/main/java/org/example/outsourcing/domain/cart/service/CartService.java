package org.example.outsourcing.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.cart.dto.request.CartSaveRequest;
import org.example.outsourcing.domain.cart.dto.request.CartUpdateRequest;
import org.example.outsourcing.domain.cart.dto.response.CartItemResponse;
import org.example.outsourcing.domain.cart.dto.response.CartResponse;
import org.example.outsourcing.domain.cart.entity.Cart;
import org.example.outsourcing.domain.cart.exception.CartException;
import org.example.outsourcing.domain.cart.exception.CartExceptionCode;
import org.example.outsourcing.domain.cart.respository.CartRepository;
import org.example.outsourcing.domain.menu.entity.Menu;
import org.example.outsourcing.domain.menu.exception.MenuException;
import org.example.outsourcing.domain.menu.exception.MenuExceptionCode;
import org.example.outsourcing.domain.menu.repository.MenuRepository;
import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.store.exception.StoreException;
import org.example.outsourcing.domain.store.exception.StoreExceptionCode;
import org.example.outsourcing.domain.store.repository.StoreRepository;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final CartRepository cartRepository;

    public CartItemResponse createCart(Long userId, Long storeId, Long menuId, CartSaveRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        Store newStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        if (!request.price().equals(menu.getPrice())) {
            throw new CartException(CartExceptionCode.INVALID_PRICE);
        }

        refreshCartByStore(user, newStore);

        Cart cart = cartRepository.save(
                Cart.builder()
                        .quantity(request.quantity())
                        .price(menu.getPrice())
                        .user(user)
                        .store(newStore)
                        .menu(menu)
                        .build()
        );

        return CartItemResponse.from(cart);
    }

    public CartResponse<CartItemResponse> getCartsByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        List<CartItemResponse> cartItems = cartRepository.findAllByUser(user)
                .stream()
                .map(CartItemResponse::from)
                .toList();

        Integer totalPrice = cartItems.stream()
                .mapToInt(item -> item.price() * item.quantity())
                .sum();

        return new CartResponse<>(cartItems, totalPrice);
    }

    @Transactional
    public CartItemResponse updateCart(Long cartId, CartUpdateRequest request) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_ITEM_NOT_FOUND));

        cart.updateCart(request.quantity());

        return CartItemResponse.from(cart);
    }

    @Transactional
    public void clearCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        List<Cart> carts = cartRepository.findAllByUser(user);

        if (carts.isEmpty()) return;

        cartRepository.deleteAllByUser(user);
    }

    @Transactional
    public void deleteCartItem(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_ITEM_NOT_FOUND));

        cartRepository.delete(cart);
    }

    private void refreshCartByStore(User user, Store newStore) {
        List<Cart> carts = cartRepository.findAllByUser(user);

        if (carts.isEmpty()) return;

        Store existingStore = carts.get(0).getStore();

        if (!existingStore.getId().equals(newStore.getId())) {
            cartRepository.deleteAllByUser(user);
        }

    }

}
