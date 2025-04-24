package org.example.outsourcing.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionCode implements ResponseCode {

    INVALID_ORDER_STATUS(false, HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태입니다."),
    ORDER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    UNDER_MINIMUM_ORDER_AMOUNT(false, HttpStatus.BAD_REQUEST, "최소 주문 금액보다 적은 금액입니다."),
    COOKING_ALREADY_STARTED(false, HttpStatus.BAD_REQUEST, "이미 조리가 시작된 주문입니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
