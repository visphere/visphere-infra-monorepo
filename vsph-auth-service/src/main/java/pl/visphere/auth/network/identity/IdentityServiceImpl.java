/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.identity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.identity.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.identity.dto.LoginResDto;
import pl.visphere.auth.network.identity.dto.RefreshReqDto;
import pl.visphere.auth.network.identity.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.security.user.AuthUserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
class IdentityServiceImpl implements IdentityService {
    private final I18nService i18nService;
    private final JwtService jwtService;

    @Override
    public LoginResDto loginViaPassword(LoginPasswordReqDto reqDto) {
        // login user via username and password

        final TokenData accessToken = jwtService.generateAccessToken(1L, "annanowak123", "annnow123@gmail.com");
        final TokenData refreshToken = jwtService.generateRefreshToken();


        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .username("annnow123")
            .emailAddress("annanowak@gmail.com")
            .profileUrl("https://raw.githubusercontent.com/Milosz08/schedule-management-server/master/_StaticPrivateContent/UserImages/zoqeCUQJ1QMqRBH4dDEB__julnow269.jpg")
            .accessToken(accessToken.token())
            .isActivated(true)
            .build();
    }

    @Override
    public LoginResDto loginViaAccessToken(AuthUserDetails userDetails) {
        // login user via access token (require JWT, protected route)

        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .username("annnow123")
            .emailAddress("annanowak@gmail.com")
            .profileUrl("https://raw.githubusercontent.com/Milosz08/schedule-management-server/master/_StaticPrivateContent/UserImages/zoqeCUQJ1QMqRBH4dDEB__julnow269.jpg")
            .accessToken("accessToken")
            .isActivated(true)
            .build();
    }

    @Override
    public RefreshResDto refresh(RefreshReqDto reqDto) {
        // refresh access token

        return RefreshResDto.builder()
            .renewAccessToken("renew accessToken")
            .build();
    }

    @Override
    public BaseMessageResDto logout(AuthUserDetails userDetails) {
        // logout user base access token (require JWT, protected route)

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.LOGOUT_RESPONSE_SUCCESS))
            .build();
    }
}
