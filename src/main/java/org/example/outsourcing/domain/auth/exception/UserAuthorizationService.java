package org.example.outsourcing.domain.auth.exception;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("auth")
public class UserAuthorizationService {

	public boolean requiredSocial(Collection<? extends GrantedAuthority> roles) {

		boolean has = roles.stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(role -> role.equals("ROLE_social"));

		if (!has) {
			throw new AuthException(AuthExceptionCode.SOCIAL_LOGIN_REQUIRED);
		}
		return true;
	}

}
