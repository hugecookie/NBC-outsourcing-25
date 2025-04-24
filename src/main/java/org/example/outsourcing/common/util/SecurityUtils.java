package org.example.outsourcing.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public static String getCurrentToken() {
		return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
	}
	public static void clearContext() {
		SecurityContextHolder.clearContext();
	}
}

//auth user 도메인 등 여러곳에서 참조되어 common 하위로 이동시켰습니다


