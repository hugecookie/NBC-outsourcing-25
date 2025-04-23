package org.example.outsourcing.domain.auth.dto;

import java.util.Collection;
import java.util.List;

import org.example.outsourcing.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserAuth {
	private final Long id;
	private final String email;
	private final List<String> roles;

	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	public static UserAuth from(User user) {
		return UserAuth.builder()
			.id(user.getId())
			.email(user.getEmail())
			.roles(user.getRoles())
			.build();
	}
}

