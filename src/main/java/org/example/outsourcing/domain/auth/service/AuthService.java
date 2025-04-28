package org.example.outsourcing.domain.auth.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.example.outsourcing.domain.auth.dto.response.TokenResponse;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.auth.dto.request.LoginRequest;
import org.example.outsourcing.domain.auth.exception.AuthException;
import org.example.outsourcing.domain.auth.exception.AuthExceptionCode;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.example.outsourcing.jwt.service.JwtService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	private record UserInfo(String email, String name) {
	}

	/**
	 * 회원 로그인<br>
	 * 유저 정보를 검증 한 후, 액세스 토큰, 리프레시 토큰 발급
	 * @param request 이메일, 비밀번호
	 * @return 액세스 토큰, 리프레시 토큰
	 * @author 박경오
	 */
	@Transactional
	public TokenResponse sighIn(LoginRequest request) {

		User user = userRepository.findByEmailAndPlatformAndIsDeleted(request.email(), Platform.LOCAL, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		checkPassword(request.password(), user.getPassword());

		return jwtService.generateToken(UserAuth.from(user), new Date());
	}

	/**
	 * 회원 로그인(소셜인증)<br>
	 * 소셜 인증을 받은 유저의 액세스 토큰, 리프레시 토큰 발급<br>
	 * 신규 로그인일 경우 자동 가입
	 * @param oAuth UserInfo
	 * @param registrationId 플랫폼
	 * @return 액세스 토큰, 리프레시 토큰
	 * @author 박경오
	 */
	@Transactional
	@PreAuthorize("@auth.requiredSocial(authentication.getAuthorities())")
	public TokenResponse socialSignIn(OAuth2User oAuth, String registrationId) {

		Platform platform;

		try {
			platform = Platform.valueOf(registrationId.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException(AuthExceptionCode.UNSUPPORTED_OAUTH_PROVIDER);
		}

		UserInfo userInfo = switch (platform) {
			case GOOGLE -> new UserInfo(
				oAuth.getAttribute("email"),
				oAuth.getAttribute("name")
			);
			case KAKAO -> {
				Map<String, Object> props = oAuth.getAttribute("properties");
				String nick = (String)props.get("nickname");
				yield new UserInfo(nick + "@kakao.com", nick);
			}
			case LOCAL -> throw new AuthException(AuthExceptionCode.UNSUPPORTED_LOCAL_AUTH_PROVIDER);
		};

		return userRepository.findByEmailAndPlatformAndIsDeleted(userInfo.email(), platform, false)
			.map(user -> jwtService.generateToken(
					UserAuth.from(user),
					new Date()
				)
			)
			.orElseGet(() -> {
				User user = userRepository.save(
					User.builder()
						.email(userInfo.email())
						.name(userInfo.name())
						.roles(List.of(UserRole.SOCIAL.getRole()))
						.password(UUID.randomUUID().toString())
						.platform(platform)
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
