package org.example.outsourcing.common.filter.constants;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterConstants {

	public static final List<String> WHITE_LIST = List.of("/api/auth/signin", "/api/users/signup", "/api/auth/oauth2/signin/google");
	public static final String REISSUE = "/api/reissue";
}