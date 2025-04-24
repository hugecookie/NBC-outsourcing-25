package org.example.outsourcing.redis.vo;

import org.example.outsourcing.redis.dto.RedisToken;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;

@RedisHash("BlackList")
@Builder
public record BlackList(

	@Id
	String accessToken,

	@TimeToLive
	Long expiration

) {

	public static BlackList from(RedisToken dto) {
		return BlackList.builder()
			.accessToken(dto.accessToken())
			.expiration(dto.timeToLive())
			.build();
	}

}
