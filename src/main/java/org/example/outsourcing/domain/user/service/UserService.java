package org.example.outsourcing.domain.user.service;

import java.util.Arrays;

import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.user.dto.request.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.request.UserModifyRequest;
import org.example.outsourcing.domain.user.dto.response.UserResponse;
import org.example.outsourcing.domain.user.dto.request.UserSaveRequest;
import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.entity.UserRole;
import org.example.outsourcing.domain.user.event.UserWithDrawEvent;
import org.example.outsourcing.domain.user.exception.UserException;
import org.example.outsourcing.domain.user.exception.UserExceptionCode;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.example.outsourcing.common.s3.S3Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher publisher;
	private final S3Service s3Service;

	@Transactional
	public void createUser(UserSaveRequest request) {

		checkEmail(request.email());

		userRepository.save(
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
				.platform(Platform.LOCAL)
				.build()
		);
	}

	@Transactional
	@PreAuthorize("@userz.checkUserId(authentication.principal.id, #request.email())")
	public void withdrawUser(UserDeleteRequest request, Long userId, String accessToken) {

		User user = userRepository.findByIdAndIsDeleted(userId, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		if (user.getPlatform() == Platform.LOCAL) {
			checkPassword(request.password(), user.getPassword());
		}

		user.withdraw();
		publisher.publishEvent(new UserWithDrawEvent(accessToken));
	}

	@Transactional
	@PreAuthorize("hasAnyRole('owner', 'admin', 'user') and @userz.checkUserId(authentication.principal.id, #request.email())")
	public void modifyUser(UserModifyRequest request) {

		User user = userRepository.findByEmailAndPlatformAndIsDeleted(
				request.email(), Platform.LOCAL, false
			)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		checkPassword(request.password(), user.getPassword());

		user.changeProfileInformation(
			request.name(),
			passwordEncoder.encode(request.newPassword())
		);
	}

	@Transactional(readOnly = true)
	public UserResponse viewUser(UserAuth userAuth) {
		User user = userRepository.findById(userAuth.getId())
				.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		String signedUrl = s3Service.generateSignedUrl(user.getProfileImgUrl());

		return UserResponse.from(user, signedUrl);
	}

	@Transactional
	public void updateUserProfileImage(MultipartFile image, Long userId, UserAuth userAuth) {

		if (!userAuth.getId().equals(userId)) {
			throw new UserException(UserExceptionCode.NO_AUTH_FOR_PROFILE_UPDATE);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		String key = s3Service.uploadFile(image);
		user.changeProfileImage(key);

		userRepository.save(user);
	}

	private void checkPassword(String rawPassword, String hashedPassword) {
		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}
	}

	private void checkEmail(String email) {
		if (userRepository.existsByEmailAndPlatform(email, Platform.LOCAL)) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}
	}
}