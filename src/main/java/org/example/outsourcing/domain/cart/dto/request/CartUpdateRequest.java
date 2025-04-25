package org.example.outsourcing.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "장바구니 수정 요청 DTO")
public record CartUpdateRequest (

        @Schema(description = "메뉴 수량")
        @NotNull(message = "수량은 필수 입력 값입니다.")
        Integer quantity

){
}
