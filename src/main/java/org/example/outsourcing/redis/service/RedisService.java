package org.example.outsourcing.redis.service;

import org.example.outsourcing.redis.dto.RedisToken;
import org.example.outsourcing.redis.repository.RedisRepository;
import org.example.outsourcing.redis.vo.BlackList;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisService {

	private final RedisRepository redisRepository;

	public void addBlackListToken(RedisToken dto) {

		redisRepository.save(BlackList.from(dto));
	}

	public boolean isBlackListed(String accessToken) {

		return redisRepository.existsById(accessToken);
	}
}
