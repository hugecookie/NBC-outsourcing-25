package org.example.outsourcing.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.outsourcing.common.s3.S3Service;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.user.dto.request.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.request.UserModifyRequest;
import org.example.outsourcing.domain.user.dto.request.UserSaveRequest;
import org.example.outsourcing.domain.user.dto.response.UserResponse;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.event.UserWithDrawEvent;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 서비스 테스트")
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private S3Service s3Service;

	private User localUser;
	private User socialUser;
	private UserAuth userAuth;
	private static UserSaveRequest userSaveRequest;
	private static UserDeleteRequest userDeleteRequest;
	private static UserModifyRequest userModifyRequest;

	private static final String ENCODED_PASSWORD = "encodedPassword";

	@Captor
	ArgumentCaptor<User> userCaptor;

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

	@BeforeAll
	static void setUpOnce() {
		userSaveRequest = new UserSaveRequest("test@unknow.com", "익명123", "P@ssw0rd1225", "user");
		userDeleteRequest = new UserDeleteRequest("test@unknow.com", "P@ssw0rd1225");
		userModifyRequest = new UserModifyRequest("test@unknow.com", "P@ssw0rd1225", "익명456", "P@ssw0rd1224");
	}

	@Nested
	@DisplayName("유저 서비스 성공 테스트")
	class UserServiceSuccessTest {

		@Test
		@DisplayName("유저 생성 성공")
		void createUser() {
			// given
			given(userRepository.existsByEmailAndPlatform(anyString(), any())).willReturn(false);
			given(passwordEncoder.encode(anyString())).willReturn(ENCODED_PASSWORD);

			// when
			userService.createUser(userSaveRequest);

			// then
			verify(userRepository, times(1)).save(userCaptor.capture());
			assertAll(
				() -> assertEquals(userSaveRequest.email(), userCaptor.getValue().getEmail()),
				() -> assertEquals(userSaveRequest.name(), userCaptor.getValue().getName()),
				() -> assertNotEquals(userSaveRequest.password(), userCaptor.getValue().getPassword()),
				() -> assertTrue(userCaptor.getValue().getRoles().contains("ROLE_" + userSaveRequest.role()))
			);
		}

		@Test
		@DisplayName("로컬 유저 탈퇴 성공")
		void withdrawUser() {
			// given
			given(userRepository.findByEmailAndIsDeleted(anyString(), anyBoolean()))
				.willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
			String beforeName = localUser.getName();

			// when
			userService.withdrawUser(userDeleteRequest, "accessToken");

			// then
			verify(publisher, times(1)).publishEvent(any(UserWithDrawEvent.class));
			assertAll(
				() -> assertNotEquals(localUser.getName(), beforeName),
				() -> assertTrue(localUser.isDeleted()),
				() -> assertTrue(localUser.getRoles().isEmpty())
			);
		}

		@Test
		@DisplayName("소셜 유저 탈퇴 성공")
		void withdrawSocialUser() {
			// given
			given(userRepository.findByEmailAndIsDeleted(anyString(), anyBoolean()))
				.willReturn(Optional.of(socialUser));
			String beforeName = socialUser.getName();

			// when
			userService.withdrawUser(userDeleteRequest, "accessToken");

			// then
			verify(publisher, times(1)).publishEvent(any(UserWithDrawEvent.class));
			assertAll(
				() -> assertNotEquals(socialUser.getEmail(), userDeleteRequest.email()),
				() -> assertNotEquals(socialUser.getName(), beforeName),
				() -> assertTrue(socialUser.isDeleted()),
				() -> assertTrue(socialUser.getRoles().isEmpty())
			);
		}

		@Test
		@DisplayName("유저 수정 성공")
		void modifyUser() {
			// given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class), anyBoolean()))
				.willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
			given(passwordEncoder.encode(anyString())).willReturn(ENCODED_PASSWORD);

			// when
			userService.modifyUser(userModifyRequest);

			// then
			assertAll(
				() -> assertEquals(localUser.getName(), userModifyRequest.name()),
				() -> assertEquals(localUser.getPassword(), ENCODED_PASSWORD)
			);
		}

		@Test
		@DisplayName("유저 조회 성공")
		void viewUser() {
			// given
			given(userRepository.findById(anyLong())).willReturn(Optional.of(localUser));

			// when
			UserResponse userResponse = userService.viewUser(userAuth);

			// then
			assertAll(
				() -> assertEquals(localUser.getName(), userResponse.name()),
				() -> assertEquals(localUser.getEmail(), userResponse.email()),
				() -> assertEquals(localUser.getPlatform(), userResponse.platform()),
				() -> assertEquals(localUser.getCreatedAt(), userResponse.createdAt())
			);
		}
	}

	@Nested
	@DisplayName("유저 서비스 실패 테스트")
	class UserServiceFailureTest {

		@Test
		@DisplayName("유저 생성 실패")
		void createUser() {
			// given
			given(userRepository.existsByEmailAndPlatform(anyString(), any())).willReturn(true);

			// when
			UserException userException = assertThrows(
				UserException.class,
				() -> userService.createUser(userSaveRequest)
			);

			// then
			assertEquals(UserExceptionCode.ALREADY_EXISTS_EMAIL, userException.getResponseCode());
		}

		@Test
		@DisplayName("로컬 유저 탈퇴 실패(비밀번호 불일치)")
		void withdrawUser() {
			// given
			given(userRepository.findByEmailAndIsDeleted(anyString(), anyBoolean()))
				.willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

			// when
			UserException userException = assertThrows(
				UserException.class,
				() -> userService.withdrawUser(userDeleteRequest, "accessToken")
			);

			// then
			assertEquals(UserExceptionCode.WRONG_PASSWORD, userException.getResponseCode());
		}

		@Test
		@DisplayName("유저 수정 실패")
		void modifyUser() {
			// given
			given(userRepository.findByEmailAndPlatformAndIsDeleted(anyString(), any(Platform.class), anyBoolean()))
				.willReturn(Optional.of(localUser));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

			// when
			UserException userException = assertThrows(
				UserException.class,
				() -> userService.modifyUser(userModifyRequest)
			);

			// then
			assertEquals(UserExceptionCode.WRONG_PASSWORD, userException.getResponseCode());
		}
	}
}