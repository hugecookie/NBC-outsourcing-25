package org.example.outsourcing.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartExceptionCode implements ResponseCode {

    CART_ITEM_NOT_FOUND(false, HttpStatus.NOT_FOUND, "카트에 해당 상품이 존재하지 않습니다."),
    CART_EMPTY(false, HttpStatus.BAD_REQUEST, "장바구니가 비어 있습니다."),
    SHOP_CLOSED(false, HttpStatus.BAD_REQUEST, "영업 시간이 아닙니다."),
    ONLY_CART_OWNER_CAN_MODIFY(false, HttpStatus.FORBIDDEN, "본인의 장바구니만 수정할 수 있습니다.");


    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
