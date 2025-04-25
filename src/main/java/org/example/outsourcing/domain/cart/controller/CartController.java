package org.example.outsourcing.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.cart.dto.request.CartSaveRequest;
import org.example.outsourcing.domain.cart.dto.request.CartUpdateRequest;
import org.example.outsourcing.domain.cart.dto.response.CartItemResponse;
import org.example.outsourcing.domain.cart.dto.response.CartResponse;
import org.example.outsourcing.domain.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    @PostMapping("/stores/{storeId}/menus/{menuId}/carts")
    @ResponseMessage("정상적으로 장바구니에 추가 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> createCart(@PathVariable Long storeId,
                                                   @PathVariable Long menuId,
                                                   @RequestBody @Valid CartSaveRequest request,
                                                   Authentication authentication) {

        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(userId, storeId, menuId, request));
    }

    @GetMapping("/carts")
    @ResponseMessage("정상적으로 장바구니 조회 처리 되었습니다.")
    public ResponseEntity<CartResponse<CartItemResponse>> getCarts(Authentication authentication) {

        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartsByUserId(userId));
    }

    @PutMapping("/menus/{menuId}/carts/{cartId}")
    @ResponseMessage("정상적으로 장바구니 변경 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> updateCart(@PathVariable Long cartId,
                                                   @RequestBody @Valid CartUpdateRequest request,
                                                   Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCart(userId, cartId, request));
    }

    @DeleteMapping("/carts")
    public ResponseEntity<Void> clearCart(Authentication authentication) {

        Long userId = getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts/{cartId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartId,
                                               Authentication authentication) {

        Long userId = getUserId(authentication);
        cartService.deleteCartItem(userId, cartId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        return userAuth.getId();
    }
}
