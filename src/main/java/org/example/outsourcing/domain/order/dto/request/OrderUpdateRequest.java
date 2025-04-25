package org.example.outsourcing.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.example.outsourcing.domain.order.entity.OrderStatus;

public record OrderUpdateRequest (

        @NotBlank(message = "상태는 필수 입력 값입니다.")
        OrderStatus orderStatus

) {
}
