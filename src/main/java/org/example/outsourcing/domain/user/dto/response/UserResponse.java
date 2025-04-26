package org.example.outsourcing.domain.user.dto.response;

import java.time.LocalDateTime;

import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(

	@Schema(description = "계정 email")
	String email,

	@Schema(description = "이름")
	String name,

	@Schema(description = "계정 생성 일자")
	LocalDateTime createdAt,

	@Schema(description = "사용중인 플랫폼")
	Platform platform,

	@Schema(description = "프로필 이미지 url")
	String profileImgUrl

) {
	public static UserResponse from(User user) {
		return UserResponse.builder()
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.name(user.getName())
			.platform(user.getPlatform())
			.profileImgUrl(user.getProfileImgUrl())
			.build();
	}
}
