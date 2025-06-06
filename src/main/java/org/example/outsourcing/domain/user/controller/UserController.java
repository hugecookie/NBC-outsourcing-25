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
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

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

	@Operation(summary = "회원탈퇴", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping
	@ResponseMessage("정상적으로 탈퇴처리 되었습니다.")
	public ResponseEntity<Void> withdrawUser(
		@RequestBody @Validated UserDeleteRequest request,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		userService.withdrawUser(request, userAuth.getId(), SecurityUtils.getCurrentToken());
		SecurityUtils.clearContext();
		return ResponseEntity.ok().build();
	}

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

	@Operation(summary = "프로필 이미지 변경 (본인만 가능)", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> updateProfileImage(
		@PathVariable Long id,
		@RequestParam MultipartFile image,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		userService.updateUserProfileImage(image, id, userAuth);
		return ResponseEntity.ok().build();
	}
}
