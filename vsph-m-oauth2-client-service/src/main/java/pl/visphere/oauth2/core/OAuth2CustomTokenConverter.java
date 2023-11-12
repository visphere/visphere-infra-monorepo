/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

@Slf4j
public class OAuth2CustomTokenConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> source) {
        final String accessToken = (String) source.get(ACCESS_TOKEN);
        final int expiredAfter = (Integer) source.get(EXPIRES_IN);

        Set<String> scopes = Collections.emptySet();
        if (source.containsKey(SCOPE)) {
            final String scope = (String) source.get(SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " ")).collect(Collectors.toSet());
        }
        final Map<String, Object> additionalParameters = new LinkedHashMap<>();
        source.entrySet().stream()
            .filter(e -> !getTokenResponseParams().contains(e.getKey()))
            .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

        final OAuth2AccessTokenResponse response = OAuth2AccessTokenResponse.withToken(accessToken)
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .expiresIn(expiredAfter)
            .scopes(scopes)
            .additionalParameters(additionalParameters)
            .build();

        log.info("Successfully generated OAuth2 token with parameters: '{}'", response);
        return response;
    }

    public Set<String> getTokenResponseParams() {
        return Set.of(ACCESS_TOKEN, TOKEN_TYPE, EXPIRES_IN, REFRESH_TOKEN, SCOPE);
    }
}
