package org.example.outsourcing.redis.dto;

import lombok.Builder;

@Builder
public record RedisToken(

	String accessToken,

	long timeToLive

) {
	public static RedisToken of(String accessToken, long timeToLive) {
		return RedisToken.builder()
			.accessToken(accessToken)
			.timeToLive(timeToLive)
			.build();
	}

}
