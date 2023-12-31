/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.service.user;

import pl.visphere.lib.kafka.payload.user.*;

public interface UserService {
    CheckUserResDto checkUser(String username);
    UserDetailsResDto getUserDetails(Long userId);
    PersistOAuth2UserResDto persistNewUser(PersistOAuth2UserReqDto reqDto);
    OAuth2UserDetailsResDto getOAuth2UserDetails(Long userId);
    LoginOAuth2UserDetailsResDto updateOAuth2UserDetails(UpdateOAuth2UserDetailsReqDto reqDto);
    LoginOAuth2UserDetailsResDto loginOAuth2User(Long userId);
    CheckUserSessionResDto checkUserSession(CheckUserSessionReqDto reqDto);
    UsersDetailsResDto getUsersDetails(UsersDetailsReqDto reqDto);
    void checkUserCredentials(CredentialsConfirmationReqDto reqDto);
}
