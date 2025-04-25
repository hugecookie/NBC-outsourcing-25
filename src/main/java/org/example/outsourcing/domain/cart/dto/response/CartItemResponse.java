package org.example.outsourcing.domain.cart.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.example.outsourcing.domain.cart.entity.Cart;

@Builder
@Schema(description = "장바구니 아이템 응답 DTO")
public record CartItemResponse (

        @Schema(description = "장바구니 아이템 ID")
        Long id,

        @Schema(description = "메뉴 이름")
        String name,

        @Schema(description = "주문 수량")
        Integer quantity,

        @Schema(description = "메뉴 가격")
        Integer price

) {
    public static CartItemResponse from(Cart cart) {
        return CartItemResponse.builder()
                .id(cart.getId())
                .name(cart.getMenu().getName())
                .quantity(cart.getQuantity())
                .price(cart.getPrice())
                .build();
    }
}
