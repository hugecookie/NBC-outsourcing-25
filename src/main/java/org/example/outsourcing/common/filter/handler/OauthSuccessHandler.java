package org.example.outsourcing.common.filter.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {

		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			String registrationId = oauthToken.getAuthorizedClientRegistrationId();

			request.setAttribute("oauth2_provider", registrationId);
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		request.getRequestDispatcher("/api/auth/social/login")
			.forward(request, response);
	}
}
