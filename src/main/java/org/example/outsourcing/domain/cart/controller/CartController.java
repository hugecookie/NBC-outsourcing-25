package org.example.outsourcing.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.cart.dto.request.CartSaveRequest;
import org.example.outsourcing.domain.cart.dto.request.CartUpdateRequest;
import org.example.outsourcing.domain.cart.dto.response.CartItemResponse;
import org.example.outsourcing.domain.cart.dto.response.CartResponse;
import org.example.outsourcing.domain.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    @PostMapping("/stores/{storeId}/menus/{menuId}/carts")
    @ResponseMessage("정상적으로 장바구니에 추가 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> createCart(@PathVariable Long storeId,
                                                   @PathVariable Long menuId,
                                                   @RequestBody @Valid CartSaveRequest request) {
        // 인증 유저 아이디 받아야함
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(userId, storeId, menuId, request));
    }

    @GetMapping("/carts")
    @ResponseMessage("정상적으로 장바구니 조회 처리 되었습니다.")
    public ResponseEntity<CartResponse<CartItemResponse>> getCarts() {
        // 인증 유저 아이디 받아야함
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartsByUserId(userId));
    }

    @PutMapping("/carts/{cartId}")
    @ResponseMessage("정상적으로 장바구니 변경 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> updateCart(@PathVariable Long cartId,
                                                   @RequestBody CartUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCart(cartId, request));
    }

    @DeleteMapping("/carts")
    public ResponseEntity<Void> clearCart() {
        // 인증 유저 아이디 받아야함
        Long userId = 1L;
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts/{cartId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
        return ResponseEntity.noContent().build();
    }

}
