package org.example.outsourcing.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record loginRequest(

	@NotBlank
	@Schema(description = "로그인 대상자의 email")
	String email,
	@NotBlank
	@Schema(description = "로그인 대상자의 비밀번호")
	String password

) {
}
