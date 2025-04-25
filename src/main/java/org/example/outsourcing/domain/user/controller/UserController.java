package org.example.outsourcing.domain.user.controller;

import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.common.util.SecurityUtils;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.user.dto.request.UserDeleteRequest;
import org.example.outsourcing.domain.user.dto.request.UserModifyRequest;
import org.example.outsourcing.domain.user.dto.response.UserResponse;
import org.example.outsourcing.domain.user.dto.request.UserSaveRequest;
import org.example.outsourcing.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원가입")
	@SecurityRequirements({})
	@PostMapping
	@ResponseMessage("정상적으로 가입처리 되었습니다.")
	public ResponseEntity<Void> createUser(
		@RequestBody @Validated UserSaveRequest request) {
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PreAuthorize("@userz.checkUserId(authentication.principal.id, #request.email())")
	@Operation(summary = "회원탈퇴", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping
	@ResponseMessage("정상적으로 탈퇴처리 되었습니다.")
	public ResponseEntity<Void> withDrawUser(
		@RequestBody @Validated UserDeleteRequest request) {
		userService.withDrawUser(request, SecurityUtils.getCurrentToken());
		SecurityUtils.clearContext();
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAnyRole('user','admin','owner') and @userz.checkUserId(authentication.principal.id, #request.email())")
	@Operation(summary = "회원정보수정", security = {@SecurityRequirement(name = "bearer-key")})
	@PatchMapping
	@ResponseMessage("정상적으로 수정되었습니다.")
	public ResponseEntity<Void> modifyUser(
		@RequestBody @Validated UserModifyRequest request) {
		userService.modifyUser(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "회원정보조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	@ResponseMessage("정상적으로 조회되었습니다.")
	public ResponseEntity<UserResponse> viewUser(
		@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.ok(userService.viewUser(userAuth));
	}
}
