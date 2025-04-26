package org.example.outsourcing.domain.auth.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.example.outsourcing.domain.auth.dto.response.TokenResponse;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.auth.dto.request.LoginRequest;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.example.outsourcing.jwt.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public TokenResponse sighIn(LoginRequest request) {

		User user = userRepository.findByEmailAndPlatformAndIsDeleted(request.email(), Platform.LOCAL, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		checkPassword(request.password(), user.getPassword());

		return jwtService.generateToken(UserAuth.from(user), new Date());
	}

	@Transactional
	public TokenResponse socialSignIn(OAuth2User oAuth) {
		String email = oAuth.getAttribute("email");
		String name = oAuth.getAttribute("name");

		return userRepository.findByEmailAndPlatformAndIsDeleted(email, Platform.GOOGLE, false)
			.map(user -> jwtService.generateToken(
					UserAuth.from(user),
					new Date()
				)
			)
			.orElseGet(() -> {
				User user = userRepository.save(
					User.builder()
						.email(email)
						.name(name)
						.roles(List.of(UserRole.SOCIAL.getRole()))
						.password(UUID.randomUUID().toString())
						.platform(Platform.GOOGLE)
						.build()
				);
				return jwtService.generateToken(
					UserAuth.from(user),
					new Date()
				);
			});
	}

	public void sighOut(String accessToken) {

		jwtService.addBlackListToken(accessToken);
	}

	public TokenResponse reissue(UserAuth userAuth, String refreshToken) {

		return jwtService.reissueToken(userAuth, refreshToken);
	}

	private void checkPassword(String rawPassword, String hashedPassword) {

		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}

	}

}
