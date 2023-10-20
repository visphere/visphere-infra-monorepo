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
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.RequestDto;
import pl.visphere.lib.kafka.payload.ResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
class IdentityServiceImpl implements IdentityService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public LoginResDto loginViaPassword(LoginPasswordReqDto reqDto) {
        // login user via username and password

        final ResponseDto response = syncQueueHandler
            .sendWithBlockThread(QueueTopic.CHECK_USER, new RequestDto("From request"), ResponseDto.class)
            .orElseThrow(RuntimeException::new);

        System.out.println(response);

        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .username("annnow123")
            .emailAddress("annanowak@gmail.com")
            .profileUrl("https://raw.githubusercontent.com/Milosz08/schedule-management-server/master/_StaticPrivateContent/UserImages/zoqeCUQJ1QMqRBH4dDEB__julnow269.jpg")
            .accessToken("accessToken")
            .refreshToken("refresh token")
            .isActivated(true)
            .build();
    }

    @Override
    public LoginResDto loginViaAccessToken(Long userId) {
        // login user via access token (require JWT, protected route)

        return LoginResDto.builder()
            .fullName("Anna Nowak")
            .username("annnow123")
            .emailAddress("annanowak@gmail.com")
            .profileUrl("https://raw.githubusercontent.com/Milosz08/schedule-management-server/master/_StaticPrivateContent/UserImages/zoqeCUQJ1QMqRBH4dDEB__julnow269.jpg")
            .accessToken("accessToken")
            .refreshToken("refresh token")
            .isActivated(true)
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
