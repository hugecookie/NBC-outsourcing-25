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
		return FilterConstants.WHITE_LIST.stream().anyMatch(uri::contains)
			|| uri.equals(FilterConstants.REISSUE);
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