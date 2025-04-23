package org.example.outsourcing.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSaveRequest(

	@NotBlank(message = "이메일을 필수 입력값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	String email,

	@NotBlank(message = "이름을 필수 입력값입니다.")
	@Size(max = 20, message = "이름 최대 20글자가 넘지 않도록 해주십시오.")
	String name,

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
		message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	String password,

	@NotBlank
	@Pattern(regexp = "^(admin|owner|user)$",
		message = "Role 입력값은 admin, owner, user 중 하나여야 합니다.")
	String role,
	String profileImgUrl

) {
}
