package org.example.outsourcing.redis.dto;

public record RedisToken(

	String accessToken,

	long timeToLive

) {
	public static RedisToken of(String accessToken, long timeToLive) {
		return new RedisToken(accessToken, timeToLive);
	}
}
