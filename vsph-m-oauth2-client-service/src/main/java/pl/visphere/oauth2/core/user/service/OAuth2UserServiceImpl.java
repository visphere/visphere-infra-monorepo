/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.oauth2.core.OAuth2Properties;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.core.user.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final OAuth2Properties oAuth2Properties;
    private final OAuth2UserLoader oAuth2UserLoader;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            final OAuth2User user = super.loadUser(userRequest);

            final OAuth2Supplier supplier = OAuth2Supplier
                .checkIfSupplierExist(userRequest, oAuth2Properties.getSuppliers());

            final UserLoaderData userLoaderData = UserLoaderData.builder()
                .supplier(supplier)
                .build();

            final OAuth2UserInfo userInfo = OAuth2UserInfo.OAuth2UserInfoFabricator
                .fabricate(supplier, user.getAttributes());

            final OAuth2UserResponse response = oAuth2UserLoader.loadAndAuthUser(userLoaderData, userInfo);
            final OAuth2UserData resData = OAuth2UserData.builder()
                .openId(userInfo.getId())
                .userDetails(response.userDetails())
                .userLoaderData(userLoaderData)
                .attributes(user.getAttributes())
                .isAlreadySignup(response.isAlreadySignup())
                .build();

            log.info("Successfully load user from OAuth2 client: '{}'.", resData);
            return resData;
        } catch (AbstractRestException ex) {
            throw new AuthenticationServiceException(ex.getPlaceholder().getHolder());
        }
    }
}
