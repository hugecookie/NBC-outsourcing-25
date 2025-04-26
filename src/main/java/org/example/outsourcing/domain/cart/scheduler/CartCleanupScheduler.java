package org.example.outsourcing.domain.cart.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.outsourcing.domain.cart.respository.CartRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CartCleanupScheduler {

    private final CartRepository cartRepository;

    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void deleteOldCarts() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        cartRepository.deleteByUpdatedAtBefore(oneHourAgo);
    }
}
