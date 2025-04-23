package org.example.outsourcing.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record loginRequest(

	@NotBlank
	String email,
	@NotBlank
	String password

) {
}
