package org.example.outsourcing.domain.auth.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public static String getCurrentToken() {
		return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
	}
	public static void clearContext() {
		SecurityContextHolder.clearContext();
	}
}

//컨텍스트 홀더에서 토큰을 가져오는 헬퍼 클래스입니다

