package org.example.outsourcing.jwt.exception;

import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenExceptionCode implements ResponseCode {

	REFRESH_TOKEN_EXPIRED(false, HttpStatus.FORBIDDEN, "리프레쉬 토큰이 만료되었습니다."),
	NOT_VALID_JWT_TOKEN(false, HttpStatus.FORBIDDEN, "옳바르지 않은 JWT 토큰입니다."),
	NOT_VALID_SIGNATURE(false, HttpStatus.FORBIDDEN, "서명이 올바르지 않습니다."),
	NOT_VALID_CONTENT(false, HttpStatus.FORBIDDEN, "내용이 올바르지 않습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
