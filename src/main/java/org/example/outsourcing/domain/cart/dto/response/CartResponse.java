package org.example.outsourcing.domain.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.example.outsourcing.domain.cart.entity.Cart;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(description = "장바구니 전체 응답 DTO")
public record CartResponse<CartItemResponse> (

        @Schema(description = "장바구니 아이템 목록")
        List<CartItemResponse> cartItems,

        @Schema(description = "총 가격")
        Integer totalPrice

){
}
