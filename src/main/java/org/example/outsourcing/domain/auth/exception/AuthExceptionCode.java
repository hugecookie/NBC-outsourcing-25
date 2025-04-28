package org.example.outsourcing.domain.auth.exception;

import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ResponseCode {

	SOCIAL_LOGIN_REQUIRED(false, HttpStatus.FORBIDDEN, "먼저 소셜 로그인 인증부터 진행해야합니다."),
	UNSUPPORTED_OAUTH_PROVIDER(false, HttpStatus.BAD_REQUEST, "지원하지 않는 인가 서버입니다."),
	UNSUPPORTED_LOCAL_AUTH_PROVIDER(false, HttpStatus.BAD_REQUEST, "로컬 서버는 소셜 로그인을 지원하지 않습니다.");

	private final boolean isSuccess;
	private final HttpStatus status;
	private final String message;
}
