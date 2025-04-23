package org.example.outsourcing.domain.user.exception;

import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UserException extends BaseException {

	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public UserException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getStatus();
	}

}
