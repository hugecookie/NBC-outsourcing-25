package org.example.outsourcing.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderSaveRequest(

        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        String phoneNumber,

        @NotBlank(message = "주소지는 필수 입력 값입니다.")
        String deliveryAddress

) {
}
