/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";

    private final Environment environment;

    public TokenData generateAccessToken(Long userId, String username, String email) {
        final Date expirationTime = getExpirationTime(JwtProperties.JWT_ACCESS_LIFE_MINUTES);
        final Claims claims = Jwts.claims();
        claims.put(JwtClaim.USER_ID.getClaim(), userId);
        claims.put(JwtClaim.USER_EMAIL.getClaim(), email);
        final String token = generateBaseToken(username, claims, expirationTime)
            .compact();
        return TokenData.builder()
            .token(token)
            .expiredAt(expirationTime)
            .build();
    }

    public TokenData generateRefreshToken() {
        return TokenData.builder()
            .token(UUID.randomUUID().toString())
            .expiredAt(getExpirationTime(JwtProperties.JWT_REFRESH_LIFE_HOURS))
            .build();
    }

    private JwtBuilder generateBaseToken(String subject, Claims claims, Date expirationTime) {
        return Jwts.builder()
            .signWith(getSignedKey(), SignatureAlgorithm.HS256)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer(JwtProperties.JWT_ISSUER.getValue(environment))
            .setAudience(JwtProperties.JWT_AUDIENCE.getValue(environment))
            .setExpiration(expirationTime);
    }

    public JwtValidateState validate(String token) {
        try {
            final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignedKey()).build()
                .parseClaimsJws(token).getBody();
            return JwtValidateState.builder()
                .claims(claims)
                .isValid(true)
                .build();
        } catch (ExpiredJwtException ex) {
            log.error("Passed token is expired. Cause: {}", ex.getMessage());
            throw new JwtException.JwtIsExpiredException();
        } catch (SignatureException ex) {
            log.error("Passed token signature is invalid. Cause: {}", ex.getMessage());
            throw new JwtException.JwtIsInvalidException();
        } catch (RuntimeException ex) {
            log.error("Passed token is malformed or corrupted. Cause: {}", ex.getMessage());
            throw new JwtException.JwtIsInvalidException();
        }
    }

    public String extractFromReq(HttpServletRequest req) {
        final String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    private Date getExpirationTime(JwtProperties life) {
        return DateUtils.addMinutes(new Date(), life.getValue(environment, Integer.class));
    }

    private Key getSignedKey() {
        final String key = JwtProperties.JWT_SECRET_KEY.getValue(environment);
        final byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}