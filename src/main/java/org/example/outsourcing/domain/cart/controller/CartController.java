package org.example.outsourcing.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    @PostMapping("/stores/{storeId}/menus/{menuId}/carts")
    @Operation(
            summary = "장바구니에 메뉴 추가",
            description = "지정한 가게(storeId)의 메뉴(menuId)를 장바구니에 추가한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 장바구니에 추가 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> createCart(@PathVariable Long storeId,
                                                   @PathVariable Long menuId,
                                                   @RequestBody @Valid CartSaveRequest request,
                                                   @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(userId, storeId, menuId, request));
    }

    @GetMapping("/carts")
    @Operation(
            summary = "장바구니 조회",
            description = "현재 로그인한 사용자의 장바구니 목록을 조회한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 장바구니 조회 처리 되었습니다.")
    public ResponseEntity<CartResponse<CartItemResponse>> getCarts(@AuthenticationPrincipal UserAuth userAuth) {

        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartsByUserId(userId));
    }

    @PutMapping("/menus/{menuId}/carts/{cartId}")
    @Operation(
            summary = "장바구니 항목 수정",
            description = "지정한 장바구니(cartId)의 수량을 수정한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ResponseMessage("정상적으로 장바구니 변경 처리 되었습니다.")
    public ResponseEntity<CartItemResponse> updateCart(@PathVariable Long cartId,
                                                   @RequestBody @Valid CartUpdateRequest request,
                                                   @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateCart(userId, cartId, request));
    }

    @DeleteMapping("/carts")
    @Operation(
            summary = "장바구니 전체 비우기",
            description = "현재 로그인한 사용자의 장바구니를 모두 비운다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserAuth userAuth) {

        Long userId = userAuth.getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts/{cartId}")
    @Operation(
            summary = "장바구니 항목 삭제",
            description = "지정한 장바구니(cartId) 항목을 삭제한다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartId,
                                               @AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        cartService.deleteCartItem(userId, cartId);
        return ResponseEntity.noContent().build();
    }

}
