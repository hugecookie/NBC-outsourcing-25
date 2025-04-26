package org.example.outsourcing.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.outsourcing.domain.auth.dto.UserAuth;

import org.example.outsourcing.domain.auth.dto.request.LoginRequest;
import org.example.outsourcing.domain.auth.dto.response.TokenResponse;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.example.outsourcing.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스 테스트")
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	private User localUser;
	private User socialUser;
	private UserAuth userAuth;

	private static OAuth2User oAuthUser;
	private static LoginRequest loginRequest;
	private static TokenResponse tokenResponse;

	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";

	@BeforeAll
	static void setUpOnce() {

		Collection<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_social")
		);

		Map<String, Object> attributes = Map.of(
			"sub", "1234567890",
			"name", "소셜123",
			"email", "test2@unknow.com"
		);

		String nameAttributeKey = "sub";

		oAuthUser = new DefaultOAuth2User(
			authorities,
			attributes,
			nameAttributeKey
		);

		loginRequest = new LoginRequest("익명@unknow.com", "P@ssw0rd1225");

		tokenResponse = TokenResponse.of(ACCESS_TOKEN, REFRESH_TOKEN);
	}

	@BeforeEach
	void setUp() {
		localUser = User.builder()
			.id(1L)
			.roles(new ArrayList<>(List.of("ROLE_user")))
			.email("test@unknow.com")
			.name("로컬123")
			.isDeleted(false)
			.platform(Platform.LOCAL)
			.password("P@ssw0rd1225")
			.build();

		socialUser = User.builder()
			.id(2L)
			.roles(new ArrayList<>(List.of("ROLE_social")))
			.email("test2@unknow.com")
			.name("소셜123")
			.isDeleted(false)
			.platform(Platform.GOOGLE)
			.password("P@ssw0rd1225")
			.build();

		userAuth = UserAuth.from(localUser);
	}

	@Nested
	@DisplayName("인증 서비스 성공 테스트")
	class AuthServiceSuccessTest {

		@Test
		@DisplayName("로그인 성공")
		void sighIn() {

			//given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class),
				anyBoolean())).willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
			given(jwtService.generateToken(any(UserAuth.class), any(Date.class))).willReturn(tokenResponse);

			//when
			TokenResponse checkTokenResponse = authService.sighIn(loginRequest);

			//then
			assertAll(
				() -> assertEquals(tokenResponse.accessToken(), checkTokenResponse.accessToken()),
				() -> assertEquals(tokenResponse.refreshToken(), checkTokenResponse.refreshToken())
			);
		}

		@Test
		@DisplayName("소셜 인증 성공(가입한 전적이 있을 경우)")
		void socialSignIn() {

			//given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class),
				anyBoolean())).willReturn(Optional.of(socialUser));
			given(jwtService.generateToken(any(UserAuth.class), any(Date.class))).willReturn(tokenResponse);

			//when
			TokenResponse checkTokenResponse = authService.socialSignIn(oAuthUser);

			//then
			assertAll(
				() -> assertEquals(tokenResponse.accessToken(), checkTokenResponse.accessToken()),
				() -> assertEquals(tokenResponse.refreshToken(), checkTokenResponse.refreshToken())
			);
		}

		@Test
		@DisplayName("소셜 인증 성공(신규 가입 후 로그인할 경우)")
		void newSocialSignIn() {

			//given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class),
				anyBoolean())).willReturn(Optional.empty());
			given(userRepository.save(any(User.class))).willReturn(socialUser);
			given(jwtService.generateToken(any(UserAuth.class), any(Date.class))).willReturn(tokenResponse);

			//when
			TokenResponse checkTokenResponse = authService.socialSignIn(oAuthUser);

			//then
			assertAll(
				() -> assertEquals(tokenResponse.accessToken(), checkTokenResponse.accessToken()),
				() -> assertEquals(tokenResponse.refreshToken(), checkTokenResponse.refreshToken())
			);
		}

		@Test
		@DisplayName("로그아웃 성공")
		void sighOut() {

			//given

			//when
			authService.sighOut(ACCESS_TOKEN);

			//then
			verify(jwtService, times(1)).addBlackListToken(anyString());
		}

		@Test
		@DisplayName("토큰 재발급 성공")
		void reissue() {

			//given
			given(jwtService.reissueToken(userAuth, REFRESH_TOKEN)).willReturn(tokenResponse);

			//when
			TokenResponse checkTokenResponse = authService.reissue(userAuth, REFRESH_TOKEN);

			//then
			verify(jwtService, times(1)).reissueToken(userAuth, REFRESH_TOKEN);
			assertEquals(tokenResponse.accessToken(), checkTokenResponse.accessToken());
		}

	}

	@Nested
	@DisplayName("인증 서비스 실패 테스트")
	class AuthServiceFailureTest {

		@Test
		@DisplayName("로그인 실패(유저 조회되지 않을 시)")
		void notFoundUserSighIn() {

			//given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class),
				anyBoolean())).willReturn(Optional.empty());

			//when
			UserException userException = assertThrows(UserException.class, () -> authService.sighIn(loginRequest));

			//then
			assertEquals(UserExceptionCode.USER_NOT_FOUND, userException.getResponseCode());
		}

		@Test
		@DisplayName("로그인 실패(비밀번호 틀릴시)")
		void passwordInvalidSighIn() {

			//given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class),
				anyBoolean())).willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

			//when
			UserException userException = assertThrows(UserException.class, () -> authService.sighIn(loginRequest));

			//then
			assertEquals(UserExceptionCode.WRONG_PASSWORD, userException.getResponseCode());
		}
	}
}