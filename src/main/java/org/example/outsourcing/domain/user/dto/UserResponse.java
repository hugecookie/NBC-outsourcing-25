package org.example.outsourcing.domain.user.dto;

import java.time.LocalDateTime;

import org.example.outsourcing.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserResponse(

	String email,
	LocalDateTime createdAt

) {
	public static UserResponse from(User user) {
		return UserResponse.builder()
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
