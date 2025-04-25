package org.example.outsourcing.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.outsourcing.domain.order.entity.OrderStatus;

public record OrderUpdateRequest (

        @NotNull(message = "상태는 필수 입력 값입니다.")
        OrderStatus orderStatus

) {
}
