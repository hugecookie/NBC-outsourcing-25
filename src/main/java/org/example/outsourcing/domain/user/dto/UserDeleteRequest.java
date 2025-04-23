package org.example.outsourcing.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequest(

	@NotBlank
	String email,
	@NotBlank
	String password

) {
}

