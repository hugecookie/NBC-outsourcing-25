package org.example.outsourcing.common.s3.exception;

import lombok.Getter;
import org.example.outsourcing.common.exception.BaseException;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
public class S3Exception extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public S3Exception(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getStatus();
	}

	@Override
	public String getMessage() {
		return responseCode.getMessage();
	}
}