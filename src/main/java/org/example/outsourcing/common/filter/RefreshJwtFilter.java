package org.example.outsourcing.common.filter;

import org.example.outsourcing.common.filter.constants.FilterConstants;
import org.example.outsourcing.jwt.constants.JwtConstants;
import org.example.outsourcing.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class RefreshJwtFilter extends BaseJwtFilter {

	public RefreshJwtFilter(JwtService jwtService) {
		super(jwtService);
	}

	@Override
	protected boolean shouldSkip(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return !uri.equals(FilterConstants.REISSUE);
	}

	@Override
	protected String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.REFRESH_HEADER);
	}

	@Override
	protected boolean shouldCheckBlackList() {
		return false;
	}
}
