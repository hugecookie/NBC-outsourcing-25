package org.example.outsourcing.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import org.example.outsourcing.common.filter.exception.FilterExceptionCode;
import org.example.outsourcing.domain.auth.dto.UserAuth;
import org.example.outsourcing.jwt.constants.JwtConstants;
import org.example.outsourcing.jwt.exception.TokenException;
import org.example.outsourcing.jwt.exception.TokenExceptionCode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenParser {

	private final SecretKey secretKey;
	private final ObjectMapper objectMapper;

	private Claims parseToken(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims();
		} catch (MalformedJwtException malformedJwtException) {
			throw new TokenException(FilterExceptionCode.MALFORMED_JWT_REQUEST);
		} catch (SignatureException signatureException) {
			throw new TokenException(TokenExceptionCode.NOT_VALID_SIGNATURE);
		} catch (UnsupportedJwtException unsupportedJwtException) {
			throw new TokenException(TokenExceptionCode.NOT_VALID_CONTENT);
		}
	}

	public boolean isTokenExpired(String token) {
		Claims claims = parseToken(token);
		return claims.getExpiration().before(new Date());
	}

	public UserAuth getUserAuth(String token) {
		Claims claims = parseToken(token);

		return UserAuth.builder()
			.id(Long.valueOf(claims.getId()))
			.email(claims.getSubject())
			.roles(objectMapper.convertValue(claims.get(JwtConstants.KEY_ROLES), new TypeReference<>() {
			}))
			.build();
	}

	public Date getTokenExpiration(String token) {
		Claims claims = parseToken(token);

		return claims.getExpiration();
	}
}