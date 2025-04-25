package org.example.outsourcing.domain.order.dto.request;

import org.example.outsourcing.domain.order.entity.OrderStatus;

public record OrderUpdateRequest (

        OrderStatus orderStatus

) {
}
