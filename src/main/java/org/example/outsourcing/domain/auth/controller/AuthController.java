package org.example.outsourcing.domain.auth.controller;

import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.auth.dto.response.TokenResponse;
import org.example.outsourcing.domain.auth.dto.request.loginRequest;
import org.example.outsourcing.domain.auth.service.AuthService;
import org.example.outsourcing.common.util.SecurityUtils;
import org.example.outsourcing.domain.user.dto.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "로그인")
	@SecurityRequirements({})
	@ResponseMessage("정상적으로 로그인이이 되었습니다.")
	@PostMapping("/signin")
	public ResponseEntity<TokenResponse> singIn(@RequestBody @Validated loginRequest request) {
		return ResponseEntity.ok(authService.sighIn(request));
	}

	@Operation(summary = "로그아웃", security = {@SecurityRequirement(name = "bearer-key")})
	@ResponseMessage("정상적으로 로그아웃 되었습니다.")
	@PostMapping("/signout")
	public ResponseEntity<Void> singOut() {
		authService.sighOut(SecurityUtils.getCurrentToken());
		SecurityUtils.clearContext();
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "토큰 재발급", security = {@SecurityRequirement(name = "Refresh-Token")})
	@ResponseMessage("정상적으로 토큰이 재발급되었습니다.")
	@PostMapping("/reissue")
	public ResponseEntity<TokenResponse> reissue(@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.ok(authService.reissue(userAuth, SecurityUtils.getCurrentToken()));
	}

	@Operation(hidden = true)
	@PreAuthorize("hasRole('social')")
	@GetMapping("/social/login")
	public ResponseEntity<TokenResponse> socialSignIn(@AuthenticationPrincipal OAuth2User oAuth) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.socialSignIn(oAuth));
	}

}
