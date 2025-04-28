package org.example.outsourcing.domain.user.exception;

import java.util.Objects;

import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component("userz")
@RequiredArgsConstructor
public class UserCrudAuthorizationService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public boolean checkUserId(Long userId, String email) {

		User user = userRepository.findByIdAndIsDeleted(userId, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		if (!Objects.equals(user.getEmail(), email)) {
			throw new UserException(UserExceptionCode.NOT_OWNED_EMAIL);
		}

		return true;
	}
}
