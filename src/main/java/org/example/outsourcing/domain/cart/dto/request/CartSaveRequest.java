package org.example.outsourcing.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record CartSaveRequest (

        @NotNull(message = "수량은 필수 입력 값입니다.")
        Integer quantity

){
}
