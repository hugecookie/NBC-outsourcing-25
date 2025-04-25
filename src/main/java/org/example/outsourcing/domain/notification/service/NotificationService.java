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

    // 알림 목록 조회 (사용자별로 알림 조회)
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);  // 사용자에 해당하는 알림 조회
        return notifications.stream()
                .map(notification -> new NotificationResponseDto(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getDescription(),
                        notification.isChecked()))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리 (알림을 읽은 상태로 업데이트)
    @Transactional
    public void markAsRead(NotificationRequestDto requestDto) {
        Notification notification = notificationRepository.findById(requestDto.notificationId())
                .orElseThrow(() -> new NotificationException(NotificationExceptionCode.NOT_FOUND_NOTIFICATION));
        notification.markAsRead();
    }

}
