/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.access;

import pl.visphere.auth.network.access.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.access.dto.LoginResDto;
import pl.visphere.auth.network.access.dto.RefreshReqDto;
import pl.visphere.auth.network.access.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;

interface IAccessService {
    LoginResDto loginViaPassword(LoginPasswordReqDto reqDto);
    LoginResDto loginViaAccessToken(Long userId);
    RefreshResDto refresh(RefreshReqDto reqDto);
    BaseMessageResDto logout(Long userId);
}
