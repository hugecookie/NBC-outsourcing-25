package org.example.outsourcing.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserModifyRequest(

	@NotBlank
	@Schema(description = "변경 대상자의 email")
	String email,

	@NotBlank
	@Schema(description = "변경 대상자의 기존 비밀번호")
	String password,

	@NotBlank(message = "변경하실 이름을 입력해주세요.")
	@Size(max = 20, message = "이름 최대 20글자가 넘지 않도록 해주십시오.")
	@Schema(description = "변경할 이름")
	String name,

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
		message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	@Schema(description = "변경할 비밀번호(최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 모두 포함)")
	String newPassword

) {
}
