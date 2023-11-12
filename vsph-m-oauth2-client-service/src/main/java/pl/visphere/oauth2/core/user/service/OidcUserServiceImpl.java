/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import pl.visphere.oauth2.core.OAuth2Properties;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.core.user.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OidcUserServiceImpl extends OidcUserService {
    private final OAuth2Properties oAuth2Properties;
    private final OAuth2UserLoader oAuth2UserLoader;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final OidcUser user = super.loadUser(userRequest);

        final OAuth2Supplier supplier = OAuth2Supplier
            .checkIfSupplierExist(userRequest, oAuth2Properties.getSuppliers());

        final UserLoaderData userLoaderData = UserLoaderData.builder()
            .supplier(supplier)
            .oidcUserInfo(user.getUserInfo())
            .oidcIdToken(user.getIdToken())
            .build();

        final OAuth2UserInfo userInfo = OAuth2UserInfo.OAuth2UserInfoFabricator
            .fabricate(supplier, user.getAttributes());

        final OAuth2UserResponse response = oAuth2UserLoader.loadAndAuthUser(userLoaderData, userInfo);
        final OAuth2UserData resData = OAuth2UserData.builder()
            .userDetails(response.userDetails())
            .userLoaderData(userLoaderData)
            .attributes(user.getAttributes())
            .isAlreadySignup(response.isAlreadySignup())
            .build();

        log.info("Successfully load user from OAuth2 (OIDC) client: '{}'", resData);
        return resData;
    }
}
