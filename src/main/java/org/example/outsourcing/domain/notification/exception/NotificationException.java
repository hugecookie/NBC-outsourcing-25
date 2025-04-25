package org.example.outsourcing.domain.notification.exception;

import lombok.Getter;
import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
public class NotificationException extends BaseException {
    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    public NotificationException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.httpStatus = responseCode.getStatus();
    }
}
