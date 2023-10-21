/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import pl.visphere.auth.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.auth.network.identity.dto.RefreshReqDto;
import pl.visphere.auth.network.identity.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

interface IdentityService {
    LoginResDto loginViaPassword(LoginPasswordReqDto reqDto);
    LoginResDto loginViaAccessToken(AuthUserDetails user);
    RefreshResDto refresh(RefreshReqDto reqDto);
    BaseMessageResDto logout(AuthUserDetails user);
}