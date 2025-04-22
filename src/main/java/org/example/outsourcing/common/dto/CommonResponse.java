package org.example.outsourcing.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T> (
	boolean success,
	int status,
	String message,
	T result
){
	public static <T> CommonResponse<T> of(boolean success, String message, int status, T result) {
		return CommonResponse.<T> builder()
			.success(success)
			.message(message)
			.status(status)
			.result(result)
			.build();
	}
}