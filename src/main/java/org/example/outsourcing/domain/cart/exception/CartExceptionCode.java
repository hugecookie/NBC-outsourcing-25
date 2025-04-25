package org.example.outsourcing.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartExceptionCode implements ResponseCode {

    CART_ITEM_NOT_FOUND(false, HttpStatus.NOT_FOUND, "카트에 해당 상품이 존재하지 않습니다."),
    INVALID_PRICE(false, HttpStatus.BAD_REQUEST, "가격이 일치하지 않습니다."),
    CART_EMPTY(false, HttpStatus.BAD_REQUEST, "장바구니가 비어 있습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
