package org.example.outsourcing.domain.order.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.example.outsourcing.domain.order.entity.Order;
import org.example.outsourcing.domain.order.entity.OrderItem;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
public class OrderListResponse {

    private final Order order;

    private final List<OrderItemResponse> orderItems;

    public OrderListResponse(Order order, List<OrderItem> orderItems) {
        this.order = order;
        this.orderItems = orderItems.stream()
                .map(OrderItemResponse::from)
                .toList();
    }

}
