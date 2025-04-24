package org.example.outsourcing.domain.cart.exception;

import lombok.Getter;
import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
public class CartException extends BaseException {

    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    public CartException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.httpStatus = responseCode.getStatus();
    }
}
