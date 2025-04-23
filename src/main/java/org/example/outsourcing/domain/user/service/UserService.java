package org.example.outsourcing.domain.user.service;

import java.util.Arrays;

import org.example.outsourcing.domain.user.dto.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.UserResponse;
import org.example.outsourcing.domain.user.dto.UserSaveRequest;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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
	public void withDrawUser(UserDeleteRequest request) {

		User user = userRepository.findByEmailAndIsDeleted(request.email(), false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		checkPassword(request.password(), user.getPassword());

		user.withdraw();
	}

	private void checkEmail(String email) {

		if (userRepository.existsByEmail(email)) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}
	}

	private void checkPassword(String rawPassword, String hashedPassword) {

		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}

	}

}
