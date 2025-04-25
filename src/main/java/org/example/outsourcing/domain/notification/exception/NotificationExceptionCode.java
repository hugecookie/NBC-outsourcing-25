package org.example.outsourcing.domain.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationExceptionCode implements ResponseCode {
    NOT_FOUND_FCMTOKEN(false, HttpStatus.NOT_FOUND, "FCM 토큰을 찾을 수 없습니다."),
    NOT_FOUND_NOTIFICATION(false, HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
