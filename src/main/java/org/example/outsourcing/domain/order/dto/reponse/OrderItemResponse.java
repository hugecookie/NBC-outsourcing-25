package org.example.outsourcing.domain.order.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.example.outsourcing.domain.order.entity.OrderItem;

@Builder
@Schema(description = "주문 아이템 응답 DTO")
public record OrderItemResponse (

        @Schema(description = "주문 아이템 ID")
        Long id,

        @Schema(description = "메뉴 이름")
        String name,

        @Schema(description = "주문 수량")
        Integer quantity,

        @Schema(description = "메뉴 가격")
        Integer price

) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .name(orderItem.getMenu().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
