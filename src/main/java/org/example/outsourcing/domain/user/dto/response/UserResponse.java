package org.example.outsourcing.domain.user.dto.response;

import java.time.LocalDateTime;

import org.example.outsourcing.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(

	@Schema(description = "계정 생성자의 email")
	String email,
	@Schema(description = "계정 생성 일자")
	LocalDateTime createdAt

) {
	public static UserResponse from(User user) {
		return new UserResponse(user.getEmail(), user.getCreatedAt());
	}
}
