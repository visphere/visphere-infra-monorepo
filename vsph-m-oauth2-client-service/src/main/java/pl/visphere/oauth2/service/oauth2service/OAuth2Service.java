/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.service.oauth2service;

import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetailsReqDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetailsResDto;

public interface OAuth2Service {
    OAuth2DetailsResDto getOAuthDetails(Long userId);
    OAuth2UsersDetailsResDto getOAuthUsersDetails(OAuth2UsersDetailsReqDto reqDto);
    void deleteOAuth2UserData(Long userId);
}
