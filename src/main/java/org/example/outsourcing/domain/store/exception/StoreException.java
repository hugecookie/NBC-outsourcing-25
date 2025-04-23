package org.example.outsourcing.domain.store.exception;

import lombok.Getter;
import org.example.outsourcing.common.exception.BaseException;
import org.springframework.http.HttpStatus;

@Getter
public class StoreException extends BaseException {
    private final StoreExceptionCode responseCode;
    private final HttpStatus httpStatus;

    public StoreException(StoreExceptionCode responseCode) {

        this.responseCode = responseCode;
        this.httpStatus = responseCode.getStatus();
    }
}
