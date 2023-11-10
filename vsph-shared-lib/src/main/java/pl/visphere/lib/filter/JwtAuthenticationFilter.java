/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.visphere.lib.jwt.JwtException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.JwtState;
import pl.visphere.lib.jwt.JwtValidateState;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.ResponseObject;
import pl.visphere.lib.kafka.payload.NullableObjectWrapper;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.UserPrincipalAuthenticationToken;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SyncQueueHandler syncQueueHandler;
    private final String[] unsecuredMatchers;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
        final String token = jwtService.extractFromReq(req);
        if (!StringUtils.hasText(token)) {
            chain.doFilter(req, res);
            return;
        }
        final JwtValidateState validateState = jwtService.validate(token);
        if (validateState.state() != JwtState.VALID) {
            throw new JwtException.JwtGeneralException(validateState.state().getPlaceholder());
        }
        final Claims claims = validateState.claims();
        final String userLogin = claims.getSubject();
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(userLogin);
        } catch (AuthenticationException ex) {
            chain.doFilter(req, res);
            return;
        }
        final NullableObjectWrapper<Boolean> isOnBlacklist = syncQueueHandler
            .sendWithBlockThread(QueueTopic.JWT_IS_ON_BLACKLIST, token, Boolean.class)
            .orElseThrow(RuntimeException::new);
        if (isOnBlacklist.response() == ResponseObject.IS_NULL || isOnBlacklist.content()) {
            chain.doFilter(req, res);
            return;
        }
        final var authToken = new UserPrincipalAuthenticationToken(req, userDetails);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        final AntPathMatcher matcher = new AntPathMatcher();
        return Arrays.stream(unsecuredMatchers)
            .anyMatch(path -> matcher.match(path, req.getServletPath()));
    }
}
