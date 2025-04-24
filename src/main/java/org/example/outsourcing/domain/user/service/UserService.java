package org.example.outsourcing.domain.user.service;

import java.util.Arrays;

import org.example.outsourcing.domain.user.dto.request.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.response.UserResponse;
import org.example.outsourcing.domain.user.dto.request.UserSaveRequest;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.event.UserWithDrawEvent;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.example.outsourcing.jwt.service.JwtService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public UserResponse createUser(UserSaveRequest request) {

		checkEmail(request.email());

		User user = userRepository.save(
			User.builder()
				.email(request.email())
				.name(request.name())
				.password(passwordEncoder.encode(request.password()))
				.roles(
					Arrays.stream(UserRole.values())
						.filter(role -> role.matches(request.role()))
						.map(UserRole::getRole)
						.toList()
				)
				.profileImgUrl(request.profileImgUrl())
				.platform(Platform.LOCAL)
				.build()
		);
		return UserResponse.from(user);
	}

	@Transactional
	public void withDrawUser(UserDeleteRequest request, String accessToken) {

		User user = userRepository.findByEmailAndPlatformAndIsDeleted(request.email(), Platform.LOCAL, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		checkPassword(request.password(), user.getPassword());
		user.withdraw();
		publisher.publishEvent(new UserWithDrawEvent(accessToken));
	}

	/* 계정 탈퇴후, 로그아웃은 이벤트 방식으로 작동합니다. Redis 의 작업은 transaction 에 포함되지 않기 때문에
	   탈퇴에 실패하고 로그아웃만 되는 현상이 발생할 수 있다고 가정하였습니다.
	   해당 이벤트는 transaction 이 커밋된 이후에 발생합니다. */

	private void checkEmail(String email) {

		if (userRepository.existsByEmailAndPlatform(email, Platform.LOCAL)) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}
	}

	private void checkPassword(String rawPassword, String hashedPassword) {

		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}
	}

}
