package org.example.outsourcing.jwt.service;

import java.util.Date;

import org.example.outsourcing.domain.auth.dto.response.TokenResponse;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.jwt.constants.TokenExpiredConstants;
import org.example.outsourcing.jwt.core.JwtTokenGenerator;
import org.example.outsourcing.jwt.core.JwtTokenParser;
import org.example.outsourcing.jwt.exception.TokenException;
import org.example.outsourcing.jwt.exception.TokenExceptionCode;
import org.example.outsourcing.redis.dto.RedisToken;
import org.example.outsourcing.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	private final JwtTokenGenerator jwtTokenGenerator;
	private final JwtTokenParser jwtParser;
	private final RedisService redisService;

	public JwtService(@Value("${spring.jwt.secret}") String secretKey, TokenExpiredConstants tokenExpiredConstant,
		RedisService redisService) {
		this.jwtTokenGenerator = new JwtTokenGenerator(Keys.hmacShaKeyFor(secretKey.getBytes()), tokenExpiredConstant);
		this.jwtParser = new JwtTokenParser(Keys.hmacShaKeyFor(secretKey.getBytes()), new ObjectMapper());
		this.redisService = redisService;
	}

	public TokenResponse generateToken(UserAuth userAuth, Date date) {

		String accessToken = jwtTokenGenerator.generateAccessToken(userAuth, date);
		String refreshToken = jwtTokenGenerator.generateRefreshToken(userAuth, date);

		return TokenResponse.of(accessToken, refreshToken);
	}

	public void addBlackListToken(String accessToken) {

		Date tokenExpiredTime = jwtParser.getTokenExpiration(accessToken);
		long now = System.currentTimeMillis();
		long ttl = (tokenExpiredTime.getTime() - now) / 1000L;

		if (ttl <= 0)
			return;

		redisService.addBlackListToken(RedisToken.of(accessToken, ttl));
	}

	public TokenResponse reissueToken(UserAuth userAuth, String refreshToken) {
		if (jwtParser.isTokenExpired(refreshToken)) {
			throw new TokenException(TokenExceptionCode.REFRESH_TOKEN_EXPIRED);
		}

		return TokenResponse.builder()
			.accessToken(jwtTokenGenerator.generateAccessToken(userAuth, new Date()))
			.build();
	}

	public boolean isBlackListed(String accessToken) {

		return redisService.isBlackListed(accessToken);
	}

	public boolean isTokenExpired(String token) {
		return jwtParser.isTokenExpired(token);
	}

	public UserAuth getUserAuth(String token) {
		return jwtParser.getUserAuth(token);
	}

}
