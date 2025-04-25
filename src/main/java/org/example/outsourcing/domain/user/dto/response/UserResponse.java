package org.example.outsourcing.domain.user.dto.response;

import java.time.LocalDateTime;

import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserResponse(

	@Schema(description = "계정 email")
	String email,

	@Schema(description = "이름")
	String name,

	@Schema(description = "계정 생성 일자")
	LocalDateTime createdAt,

	@Schema(description = "사용중인 플랫폼")
	Platform platform

) {
	public static UserResponse from(User user) {
		return UserResponse.builder()
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.name(user.getName())
			.platform(user.getPlatform())
			.build();
	}
}
