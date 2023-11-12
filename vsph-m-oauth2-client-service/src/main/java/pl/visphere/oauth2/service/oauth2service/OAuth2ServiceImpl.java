/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.service.oauth2service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.oauth2.core.user.OAuth2UserInfo;
import pl.visphere.oauth2.core.user.OAuth2UserLoader;
import pl.visphere.oauth2.core.user.OAuth2UserResponse;
import pl.visphere.oauth2.core.user.UserLoaderData;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service, OAuth2UserLoader {
    @Override
    public OAuth2UserResponse loadAndAuthUser(UserLoaderData userLoaderData, OAuth2UserInfo userInfo) {
        // TODO: login or register oauth2 user

        System.out.println(userLoaderData);
        System.out.println(userInfo);

        return OAuth2UserResponse.builder()
            .userDetails(null)
            .isAlreadySignup(false)
            .build();
    }
}
