package org.example.outsourcing.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse (

	@Schema(description = "AccessToken(인증용도)")
	String accessToken,
	@Schema(description = "RefreshToken(토큰 재발급용)")
	String refreshToken

) {
	public static TokenResponse of(String accessToken, String refreshToken) {
		return TokenResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

}


