/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.identity;

import jakarta.servlet.http.HttpServletRequest;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.network.LoginResDto;
import pl.visphere.user.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.user.network.identity.dto.RefreshReqDto;
import pl.visphere.user.network.identity.dto.RefreshResDto;

interface IdentityService {
    LoginResDto loginViaPassword(LoginPasswordReqDto reqDto);
    LoginResDto loginViaAccessToken(HttpServletRequest req, AuthUserDetails user);
    RefreshResDto refresh(RefreshReqDto reqDto);
    BaseMessageResDto logout(HttpServletRequest req, AuthUserDetails user);
}
