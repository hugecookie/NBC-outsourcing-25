package org.example.outsourcing.common.s3.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.exception.ResponseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ExceptionCode implements ResponseCode {

	NOT_SUPPORTED_FORMAT(false, HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (jpg, jpeg, png 만 허용)"),
	FILE_TOO_LARGE(false, HttpStatus.BAD_REQUEST, "파일 용량이 너무 큽니다. (최대 10MB 허용)"),
	UPLOAD_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 실패");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
