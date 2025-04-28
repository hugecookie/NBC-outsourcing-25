package org.example.outsourcing.domain.order.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.outsourcing.domain.order.dto.reponse.OrderResponse;
import org.example.outsourcing.domain.order.exception.OrderException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class OrderApiLoggerAspect {

    @Around("@annotation(org.example.outsourcing.common.annotation.OrderLoggingTarget)")
    public Object doOrderLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestUrl = request.getRequestURL().toString();
        LocalDateTime requestTime = LocalDateTime.now();

        Long orderId = null;
        Long storeId = null;
        Object body = null;

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?> responseEntity) {
                body = responseEntity.getBody();
            }

            // void
            if (body == null) {
                Object[] args = joinPoint.getArgs();
                for (Object arg : args) {
                    if (arg instanceof Long) {
                        orderId = (Long) arg;
                    }
                }
                log.info("[ORDER LOG] 요청 시각: {}, 주문 ID: {}", requestTime, orderId);
                return null;
            }

            if (body instanceof OrderResponse response) {
                orderId = response.getOrderId();
                storeId = response.getStoreId();
            }

            log.info("[ORDER LOG] 요청 시각: {}, 가게 ID: {}, 주문 ID: {}", requestTime, storeId, orderId);
            return result;

        } catch (OrderException e) {
            log.error("[ORDER LOG] 예외 발생 - 시각: {}, URL: {}, 에러: {}", requestTime, requestUrl, e.getMessage(), e);
            throw e;
        }
    }
}

