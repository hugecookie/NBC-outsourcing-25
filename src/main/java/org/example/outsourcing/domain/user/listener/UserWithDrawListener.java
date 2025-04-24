package org.example.outsourcing.domain.user.listener;

import org.example.outsourcing.domain.user.event.UserWithDrawEvent;
import org.example.outsourcing.jwt.service.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserWithDrawListener {

	private final JwtService jwtService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlerUserWithDrawEvent(UserWithDrawEvent event) {

		try {
			jwtService.addBlackListToken(event.accessToken());
		} catch (Exception e) {
			log.warn("계정 삭제 후 로그아웃 처리 중 오류 발생", e);
		}

	}
}
