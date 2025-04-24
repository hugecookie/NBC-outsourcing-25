package org.example.outsourcing.domain.order.dto.reponse;

import lombok.Builder;
import org.example.outsourcing.domain.order.entity.OrderItem;

@Builder
public record OrderItemResponse (

        Long id,

        String name,

        Integer quantity,

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
