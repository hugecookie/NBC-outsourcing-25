package org.example.outsourcing.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.notification.dto.request.NotificationRequestDto;
import org.example.outsourcing.domain.notification.dto.response.NotificationResponseDto;
import org.example.outsourcing.domain.notification.entity.Notification;
import org.example.outsourcing.domain.notification.repository.NotificationRepository;
import org.example.outsourcing.domain.notification.exception.NotificationException;
import org.example.outsourcing.domain.notification.exception.NotificationExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 목록 조회 (사용자별)
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> new NotificationResponseDto(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getDescription(),
                        notification.isChecked()))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리 (본인 알림만 읽을 수 있도록 userId 검증 추가)
    @Transactional
    public void markAsRead(Long userId, NotificationRequestDto requestDto) {
        Notification notification = notificationRepository.findById(requestDto.notificationId())
                .orElseThrow(() -> new NotificationException(NotificationExceptionCode.NOT_FOUND_NOTIFICATION));

        // userId 검증 추가
        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationException(NotificationExceptionCode.NO_AUTH_FOR_NOTIFICATION);
        }

        notification.markAsRead();
    }
}
