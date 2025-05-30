package org.example.outsourcing.common.filter.exception;

import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FilterException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public FilterException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getStatus();
	}
}