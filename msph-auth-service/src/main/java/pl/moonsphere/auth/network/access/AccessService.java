/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.moonsphere.auth.i18n.LocaleSet;
import pl.moonsphere.auth.network.access.dto.LoginPasswordReqDto;
import pl.moonsphere.auth.network.access.dto.LoginResDto;
import pl.moonsphere.auth.network.access.dto.RefreshReqDto;
import pl.moonsphere.auth.network.access.dto.RefreshResDto;
import pl.moonsphere.lib.dto.BaseMessageResDto;
import pl.moonsphere.lib.i18n.I18nService;

@Slf4j
@Service
@RequiredArgsConstructor
class AccessService implements IAccessService {
    private final I18nService i18nService;

    @Override
    public LoginResDto loginViaPassword(LoginPasswordReqDto reqDto) {
        // login user via username and password

        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .profileUrl("file://")
            .accessToken("accessToken")
            .refreshToken("refresh token")
            .build();
    }

    @Override
    public LoginResDto loginViaAccessToken(Long userId) {
        // login user via access token (require JWT, protected route)

        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .profileUrl("file://")
            .accessToken("accessToken")
            .refreshToken("refresh token")
            .build();
    }

    @Override
    public RefreshResDto refresh(RefreshReqDto reqDto) {
        // refresh access token

        return RefreshResDto.builder()
            .renewAccessToken("renew accessToken")
            .refreshToken("refresh token")
            .build();
    }

    @Override
    public BaseMessageResDto logout(Long userId) {
        // logout user base access token (require JWT, protected route)

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.LOGOUT_RESPONSE_SUCCESS))
            .build();
    }
}
