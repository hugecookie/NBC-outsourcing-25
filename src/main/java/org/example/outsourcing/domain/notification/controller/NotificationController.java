package org.example.outsourcing.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.domain.notification.dto.request.NotificationRequestDto;
import org.example.outsourcing.domain.notification.dto.response.NotificationResponseDto;
import org.example.outsourcing.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회 (사용자별)
    @ResponseMessage("알림 목록을 성공적으로 조회했습니다.")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(@AuthenticationPrincipal UserAuth userAuth) {
        Long userId = userAuth.getId();
        List<NotificationResponseDto> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @ResponseMessage("알림을 읽었습니다.")
    @PutMapping
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserAuth userAuth,
            @RequestBody NotificationRequestDto requestDto
    ) {
        Long userId = userAuth.getId();
        notificationService.markAsRead(userId, requestDto);
        return ResponseEntity.ok().build();
    }
}
