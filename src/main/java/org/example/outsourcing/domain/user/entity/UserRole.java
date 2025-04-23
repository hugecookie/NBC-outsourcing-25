package org.example.outsourcing.domain.user.entity;

import java.util.function.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	ADMIN("ROLE_admin", "admin"::equals),
	USER("ROLE_user", "user"::equals),
	OWNER("ROLE_owner", "owner"::equals);

	private final String role;
	private final Predicate<String> func;

	public boolean matches(String role) {
		return func.test(role);
	}
}
