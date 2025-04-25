package org.example.outsourcing.domain.menu.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MenuExceptionCode implements ResponseCode {

    MENU_NOT_FOUND(false, HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    MENU_FORBIDDEN(false, HttpStatus.FORBIDDEN, "해당 메뉴에 대한 권한이 없습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
