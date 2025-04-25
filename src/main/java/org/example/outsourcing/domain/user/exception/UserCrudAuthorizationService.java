package org.example.outsourcing.domain.user.exception;

import java.util.Objects;

import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("userz")
@RequiredArgsConstructor
public class UserCrudAuthorizationService {

	private final UserRepository userRepository;

	public boolean checkUserId(Long userId, String email) {

		User user = userRepository.findByEmailAndIsDeleted(email, false)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		if (!Objects.equals(userId, user.getId())) {
			throw new UserException(UserExceptionCode.NOT_OWNED_ID);
		}

		return true;
	}

}
