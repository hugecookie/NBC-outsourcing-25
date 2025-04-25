package org.example.outsourcing.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequest(

	@NotBlank
	@Schema(description = "삭제 대상자의 email")
	String email,

	@Schema(description = "삭제 대상자의 비밀번호")
	String password

) {
	public UserDeleteRequest {
		if(password == null) {
			password = "default";
		}
	}
}