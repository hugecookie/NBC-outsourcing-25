package org.example.outsourcing.domain.notification.repository;

import org.example.outsourcing.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 알림 목록 조회
    List<Notification> findByUserId(Long userId);  // 사용자 ID를 기준으로 알림 목록 조회
}
