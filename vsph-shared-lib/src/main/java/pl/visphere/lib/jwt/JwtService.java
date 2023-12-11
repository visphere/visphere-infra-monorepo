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
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String X_REFRESH_TOKEN = "X-RefreshToken";

    private final Environment environment;

    public TokenData generateAccessToken(Long userId, String username, String email) {
        final Date expirationTime = DateUtils
            .addMinutes(new Date(), JwtProperties.JWT_ACCESS_LIFE_MINUTES.getValue(environment, Integer.class));
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
        final Date expirationTime = DateUtils
            .addDays(new Date(), JwtProperties.JWT_REFRESH_LIFE_DAYS.getValue(environment, Integer.class));
        return TokenData.builder()
            .token(UUID.randomUUID().toString())
            .expiredAt(expirationTime)
            .build();
    }

    public JwtBuilder generateNonExpiredToken(String subject, Claims claims) {
        return generateBaseToken(subject, claims, null);
    }

    private JwtBuilder generateBaseToken(String subject, Claims claims, Date expirationTime) {
        final JwtBuilder jwtBuilder = Jwts.builder()
            .signWith(getSignedKey(), SignatureAlgorithm.HS256)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer(JwtProperties.JWT_ISSUER.getValue(environment))
            .setAudience(JwtProperties.JWT_AUDIENCE.getValue(environment));
        if (expirationTime != null) {
            jwtBuilder.setExpiration(expirationTime);
        }
        return jwtBuilder;
    }

    public TokenData generateOAuth2TemporaryToken(String openId, Long userId, String supplier) {
        final Date expirationTime = DateUtils
            .addMinutes(new Date(), JwtProperties.JWT_ACCESS_LIFE_MINUTES.getValue(environment, Integer.class));
        final Claims claims = Jwts.claims();
        claims.put(JwtClaim.OAUTH2_SUPPLIER.getClaim(), supplier);
        claims.put(JwtClaim.USER_ID.getClaim(), userId);
        final String token = generateBaseToken(openId, claims, expirationTime)
            .compact();
        return TokenData.builder()
            .token(token)
            .expiredAt(expirationTime)
            .build();
    }

    public JwtValidateState validate(String token) {
        Claims claims = null;
        JwtState state = JwtState.VALID;
        try {
            claims = Jwts.parserBuilder()
                .setSigningKey(getSignedKey()).build()
                .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            log.error("Passed token is expired. Cause: '{}'.", ex.getMessage());
            state = JwtState.EXPIRED;
            claims = ex.getClaims();
        } catch (SignatureException ex) {
            log.error("Passed token signature is invalid. Cause: '{}'.", ex.getMessage());
            state = JwtState.INVALID;
        } catch (RuntimeException ex) {
            log.error("Passed token is malformed or corrupted. Cause: '{}'.", ex.getMessage());
            state = JwtState.INVALID;
        }
        return JwtValidateState.builder()
            .claims(claims)
            .state(state)
            .build();
    }

    public Optional<Claims> extractClaims(String token) {
        final JwtValidateState validated = validate(token);
        return Optional.ofNullable(validated.claims());
    }

    public ZonedDateTime convertToZonedDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZonedDateTime.now().getZone());
    }

    public <T> T getClaim(Claims claims, JwtClaim claim, Class<T> claimClazz) {
        return claims.get(claim.getClaim(), claimClazz);
    }

    public String getClaim(Claims claims, JwtClaim claim) {
        return claims.get(claim.getClaim(), String.class);
    }

    public String extractFromReq(HttpServletRequest req) {
        final String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    public String extractRefreshFromReq(HttpServletRequest req) {
        final String refreshToken = req.getHeader(X_REFRESH_TOKEN);
        if (refreshToken == null) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return refreshToken;
    }

    private Key getSignedKey() {
        final String key = JwtProperties.JWT_SECRET_KEY.getValue(environment);
        final byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
