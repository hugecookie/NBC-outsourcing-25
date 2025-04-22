package org.example.outsourcing.jwt.exception;

import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class TokenException extends BaseException {

	private final ResponseCode responseCode;
	private final String message;
	private final HttpStatus httpStatus;

	public TokenException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.message = responseCode.getMessage();
		this.httpStatus = responseCode.getStatus();
	}
}
