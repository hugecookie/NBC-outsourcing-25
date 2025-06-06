package org.example.outsourcing.common.filter.constants;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterConstants {

	public static final List<String> WHITE_LIST = List.of(
		"/api/auth/signin",
		"/resources",
		"/swagger-ui",
		"/v3/api-docs",
		"/swagger-resources",
		"/webjars",
		"/api/auth/social/login"
	);

	public static final String USER_CRUD = "/api/users";

	public static final String REISSUE = "/api/auth/reissue";
}







