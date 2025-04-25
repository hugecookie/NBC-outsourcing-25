package org.example.outsourcing.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreExceptionCode implements ResponseCode {

    NO_AUTH_FOR_STORE_CREATION(false, HttpStatus.FORBIDDEN, "사장님 권한이 없습니다."),
    STORE_LIMIT_EXCEEDED(false, HttpStatus.BAD_REQUEST, "가게는 최대 3개까지만 등록할 수 있습니다."),
    STORE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "존재하지 않는 가게입니다."),
    NO_AUTH_FOR_STORE_MODIFICATION(false, HttpStatus.FORBIDDEN, "해당 가게에 대한 수정 권한이 없습니다."),
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");

    private final boolean success;
    private final HttpStatus status;
    private final String message;
}
