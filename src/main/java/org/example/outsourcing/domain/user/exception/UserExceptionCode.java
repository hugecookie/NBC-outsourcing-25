package org.example.outsourcing.domain.user.exception;

import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptionCode implements ResponseCode {

	WRONG_PASSWORD(false, HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
	NOT_OWNED_ID(false, HttpStatus.FORBIDDEN, "현재 로그인한 계정의 이메일을 입력하십시오."),
	USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
	NO_AUTH_FOR_PROFILE_UPDATE(false, HttpStatus.FORBIDDEN, "본인의 프로필만 수정할 수 있습니다."),
	ALREADY_EXISTS_EMAIL(false, HttpStatus.CONFLICT, "이미 존재하는 이메일이 있습니다.");

	private final boolean isSuccess;
	private final HttpStatus status;
	private final String message;
}
