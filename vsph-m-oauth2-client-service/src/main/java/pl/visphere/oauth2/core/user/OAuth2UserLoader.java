/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

public interface OAuth2UserLoader {
    OAuth2UserResponse loadAndAuthUser(UserLoaderData userLoaderData, OAuth2UserInfo userInfo);
}
