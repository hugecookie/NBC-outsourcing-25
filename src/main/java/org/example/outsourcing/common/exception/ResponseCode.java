package org.example.outsourcing.common.exception;

import org.springframework.http.HttpStatus;

public interface ResponseCode {

	HttpStatus getStatus();

	String getMessage();

	boolean isSuccess();
}
