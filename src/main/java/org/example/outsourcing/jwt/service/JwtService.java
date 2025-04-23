package org.example.outsourcing.jwt.service;

import java.util.Date;

import org.example.outsourcing.domain.auth.dto.TokenResponse;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.jwt.constants.TokenExpiredConstants;
import org.example.outsourcing.jwt.core.JwtTokenGenerator;
import org.example.outsourcing.jwt.core.JwtTokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	private final JwtTokenGenerator jwtTokenGenerator;
	private final JwtTokenParser jwtParser;
	private final TokenExpiredConstants tokenExpiredConstant;

	public JwtService(@Value("${spring.jwt.secret}") String secretKey, TokenExpiredConstants tokenExpiredConstant) {
		this.jwtTokenGenerator = new JwtTokenGenerator(Keys.hmacShaKeyFor(secretKey.getBytes()), tokenExpiredConstant);
		this.jwtParser = new JwtTokenParser(Keys.hmacShaKeyFor(secretKey.getBytes()), new ObjectMapper());
		this.tokenExpiredConstant = tokenExpiredConstant;
	}

	public TokenResponse generateToken(UserAuth userAuth, Date date) {

		String accessToken = jwtTokenGenerator.generateAccessToken(userAuth, date);
		String refreshToken = jwtTokenGenerator.generateRefreshToken(userAuth, date);

		return TokenResponse.of(accessToken, refreshToken);
	}

	// TODO : REDIS 추가후 작성 예정
	public void addBlackListToken(String accessToken) {

	}

	public TokenResponse reissueToken(UserAuth userAuth, String refreshToken) {
		return TokenResponse.builder()
			.accessToken(jwtTokenGenerator.generateAccessToken(userAuth, new Date()))
			.build();
	}

	/*
		// TODO : REDIS 추가후 작성 예정
		public boolean isBlackListed(String accessToken) {

		}
	*/
	public boolean isTokenExpired(String token) {
		return jwtParser.isTokenExpired(token);
	}

	public UserAuth getUserAuth(String token) {
		return jwtParser.getUserAuth(token);
	}

}
