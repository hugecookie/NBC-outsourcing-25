package org.example.outsourcing.domain.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.example.outsourcing.domain.cart.entity.Cart;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CartResponse<CartItemResponse> (

        List<CartItemResponse> cartItems,

        Integer totalPrice

){
}
