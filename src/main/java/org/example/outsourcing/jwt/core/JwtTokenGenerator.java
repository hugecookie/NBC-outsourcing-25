package org.example.outsourcing.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.jwt.constants.JwtConstants;
import org.example.outsourcing.jwt.constants.TokenExpiredConstants;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenGenerator {

	private final SecretKey secretKey;
	private final TokenExpiredConstants tokenExpiredConstant;

	public String generateAccessToken(UserAuth userAuth, Date date) {
		return buildToken(userAuth, JwtConstants.ACCESS_TOKEN, date,
			tokenExpiredConstant.getAccessTokenExpirationTime(date));
	}

	public String generateRefreshToken(UserAuth userAuth, Date date) {
		return buildToken(userAuth, JwtConstants.REFRESH_TOKEN, date,
			tokenExpiredConstant.getRefreshTokenExpirationTime(date));
	}

	private String buildToken(UserAuth userAuth, String tokenType, Date date, Date TokenExpiredTime) {
		return Jwts.builder()
			.subject(userAuth.getEmail())
			.id(userAuth.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, tokenType)
			.claim(JwtConstants.KEY_ROLES, userAuth.getRoles())
			.expiration(TokenExpiredTime)
			.issuedAt(date)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}
}
