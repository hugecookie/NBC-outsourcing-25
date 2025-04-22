package org.example.outsourcing.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// ✅ 1. CustomException 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException ex, HttpServletRequest request) {
		return ResponseEntity
				.status(ex.getErrorCode().getStatus())
				.body(ErrorResponseDto.from(ex.getErrorCode(), request.getRequestURI()));
	}

	// ✅ 2. @Valid 검증 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex,
																	  HttpServletRequest request) {
		return ResponseEntity
				.badRequest()
				.body(ErrorResponseDto.from(ex, request.getRequestURI()));
	}
}