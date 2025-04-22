package org.example.outsourcing.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // ✅ 예외 메시지를 부모(RuntimeException)에 전달
        this.errorCode = errorCode;
    }
}