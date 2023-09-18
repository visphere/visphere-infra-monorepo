/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.access;

import pl.moonsphere.auth.network.access.dto.LoginPasswordReqDto;
import pl.moonsphere.auth.network.access.dto.LoginResDto;
import pl.moonsphere.auth.network.access.dto.RefreshReqDto;
import pl.moonsphere.auth.network.access.dto.RefreshResDto;
import pl.moonsphere.lib.BaseMessageResDto;

interface IAccessService {
    LoginResDto loginViaPassword(LoginPasswordReqDto reqDto);
    LoginResDto loginViaAccessToken(Long userId);
    RefreshResDto refresh(RefreshReqDto reqDto);
    BaseMessageResDto logout(Long userId);
}
