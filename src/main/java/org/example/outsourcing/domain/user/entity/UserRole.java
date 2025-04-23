package org.example.outsourcing.domain.user.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRole {
	public static final String ADMIN = "ROLE_admin";
	public static final String OWNER = "ROLE_owner";
	public static final String USER = "ROLE_user";
}