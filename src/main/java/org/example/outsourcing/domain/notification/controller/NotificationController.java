package org.example.outsourcing.domain.notification.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.domain.notification.dto.request.NotificationRequestDto;
import org.example.outsourcing.domain.notification.dto.response.NotificationResponseDto;
import org.example.outsourcing.domain.notification.service.NotificationService;
import org.example.outsourcing.domain.user.entity.User;
import org.example.outsourcing.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // 알림 목록 조회 (사용자별)
    @ResponseMessage("알림 목록을 성공적으로 조회했습니다.")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(@RequestParam Long userId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("테스트 유저 없음"));
        List<NotificationResponseDto> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @ResponseMessage("알림을 읽었습니다.")
    @PutMapping
    public ResponseEntity<Void> markAsRead(@RequestBody NotificationRequestDto requestDto) {
        notificationService.markAsRead(requestDto);
        return ResponseEntity.ok().build();
    }
}
