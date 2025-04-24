package org.example.outsourcing.domain.cart.dto.response;


import lombok.Builder;
import org.example.outsourcing.domain.cart.entity.Cart;

@Builder
public record CartItemResponse (

        Long id,

        String name,

        Integer quantity,

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
