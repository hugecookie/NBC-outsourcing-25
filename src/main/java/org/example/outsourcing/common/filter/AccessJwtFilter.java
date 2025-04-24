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

		return (FilterConstants.WHITE_LIST.stream()
			.anyMatch(uri::contains)
			&& !method.equals("DELETE"))
			|| uri.equals(FilterConstants.REISSUE);
	}

	//튜터님 조언으로 /api/users 하나로 생성 삭제를 둘다 수행하기 때문에 method 가 DELETE 일때는 인증을 SKIP 하지 않게끔 조치했습니다.

	@Override
	protected String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.AUTH_HEADER);
	}

	@Override
	protected boolean shouldCheckBlackList() {
		return true;
	}
}