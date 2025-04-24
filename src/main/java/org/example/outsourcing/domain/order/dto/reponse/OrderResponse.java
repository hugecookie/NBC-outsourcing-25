package org.example.outsourcing.domain.order.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.example.outsourcing.domain.order.entity.Order;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OrderResponse (

        Order order,

        List<OrderItemResponse> orderItems

) {
}

