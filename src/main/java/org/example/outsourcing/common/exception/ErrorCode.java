package org.example.outsourcing.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	// ✅ 인증 로직 관련 에러
	AUTH_UNAUTHORIZED("인증이 필요한 요청입니다", HttpStatus.UNAUTHORIZED),
	AUTH_TOKEN_EXPIRED("만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	INVALID_TOKEN("토큰이 유효하지 않습니다. 다시 로그인 해주세요", HttpStatus.UNAUTHORIZED),
	FORBIDDEN("권한 없는 유저입니다.", HttpStatus.FORBIDDEN),

	// ✅ 유저 관련 에러
	USER_NOT_FOUND("존재하지 않는 사용자입니다", HttpStatus.BAD_REQUEST),
	PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
	DUPLICATED_EMAIL("이미 등록된 이메일입니다.", HttpStatus.CONFLICT),
	SAME_PASSWORD("이전 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
	EMPTY_PROFILE_IMAGE("프로필 이미지가 비어 있습니다!", HttpStatus.BAD_REQUEST),
	IMAGE_SAVE_FAIL("이미지 저장에 실패 했습니다!", HttpStatus.INTERNAL_SERVER_ERROR),

	// ✅ 장바구니 관련 에러

	// ✅ 500 서버
	INTERNAL_SERVER_ERROR("처리하지 못한 예외가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String message;
	private final HttpStatus status;
}