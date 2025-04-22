package org.example.outsourcing.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse (

	String accessToken,
	String refreshToken

) {
	public static TokenResponse of(String accessToken, String refreshToken) {
		return TokenResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

}


