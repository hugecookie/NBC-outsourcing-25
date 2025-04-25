package org.example.outsourcing.domain.menu.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MenuExceptionCode implements ResponseCode {

    MENU_NOT_FOUND(false, HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    ONLY_STORE_OWNER_CAN_MODIFY(false, HttpStatus.FORBIDDEN, "가게 사장만 메뉴를 수정할 수 있습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
