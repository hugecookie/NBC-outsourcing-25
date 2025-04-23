package org.example.outsourcing.domain.auth.controller;

import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.TokenResponse;
import org.example.outsourcing.domain.auth.dto.loginRequest;
import org.example.outsourcing.domain.auth.service.AuthService;
import org.example.outsourcing.domain.auth.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@ResponseMessage("정상적으로 로그인이이 되었습니다.")
	@PostMapping("/signin")
	public ResponseEntity<TokenResponse> singIn(@RequestBody @Validated loginRequest request) {
		return ResponseEntity.ok(authService.sighIn(request));
	}

	@ResponseMessage("정상적으로 로그아웃 되었습니다.")
	@PostMapping("/signout")
	public ResponseEntity<Void> singOut() {
		authService.sighOut(SecurityUtils.getCurrentToken());
		SecurityUtils.clearContext();
		return ResponseEntity.ok().build();
	}

}
