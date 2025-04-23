package org.example.outsourcing.domain.user.controller;

import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.user.dto.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.UserResponse;
import org.example.outsourcing.domain.user.dto.UserSaveRequest;
import org.example.outsourcing.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	@ResponseMessage("정상적으로 가입처리 되었습니다.")
	public ResponseEntity<UserResponse> createUser(@RequestBody @Validated UserSaveRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
	}

	@DeleteMapping
	@ResponseMessage("정상적으로 탈퇴처리 되었습니다.")
	public ResponseEntity<Void> withDrawUser(@RequestBody @Validated UserDeleteRequest request) {
		userService.withDrawUser(request);
		return ResponseEntity.ok().build();
	}
}
