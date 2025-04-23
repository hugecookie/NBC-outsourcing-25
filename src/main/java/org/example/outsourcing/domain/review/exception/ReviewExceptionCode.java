package org.example.outsourcing.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ReviewExceptionCode implements ResponseCode {
    REVIEW_NOT_FOUND(false, HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    STORE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
    INVALID_REVIEW_CONDITION(false, HttpStatus.BAD_REQUEST, "배달 완료된 주문만 리뷰를 작성할 수 있습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
