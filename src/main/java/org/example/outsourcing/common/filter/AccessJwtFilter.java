package org.example.outsourcing.common.filter;

import org.example.outsourcing.common.filter.constants.FilterConstants;
import org.example.outsourcing.jwt.constants.JwtConstants;
import org.example.outsourcing.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class AccessJwtFilter extends BaseJwtFilter {

	public AccessJwtFilter(JwtService jwtService) {
		super(jwtService);
	}

	@Override
	protected boolean shouldSkip(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String method = request.getMethod();

		// ✅ 프로필 이미지 경우에는 WHITE_LIST 거치기 전에 skip 안함
		if (uri.matches("^/api/users/\\d+/profile-image$") && method.equals("PUT")) {
			return false;
		}

		return (FilterConstants.WHITE_LIST.stream()
			.anyMatch(uri::contains))
			|| (uri.equals(FilterConstants.USER_CRUD) && method.equals("POST"))
			|| (uri.equals(FilterConstants.REISSUE));
	}

	@Override
	protected String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.AUTH_HEADER);
	}

	@Override
	protected boolean shouldCheckBlackList() {
		return true;
	}
}