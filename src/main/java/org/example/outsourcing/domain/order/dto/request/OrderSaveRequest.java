package org.example.outsourcing.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrderSaveRequest(

        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "유효한 휴대폰 번호 형식이 아닙니다."
        )
        String phoneNumber,

        @NotBlank(message = "주소지는 필수 입력 값입니다.")
        String deliveryAddress

) {
}
