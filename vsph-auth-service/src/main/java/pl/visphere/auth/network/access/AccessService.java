/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.access.dto.LoginPasswordReqDto;
import pl.visphere.auth.network.access.dto.LoginResDto;
import pl.visphere.auth.network.access.dto.RefreshReqDto;
import pl.visphere.auth.network.access.dto.RefreshResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;

@Slf4j
@Service
@RequiredArgsConstructor
class AccessService implements IAccessService {
    private final I18nService i18nService;

    private final KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public LoginResDto loginViaPassword(LoginPasswordReqDto reqDto) {
        // login user via username and password

        log.info("Producing message: {}", "This is a test message");
        kafkaTemplate.send("testtopic", "key", "This is a test message");
        

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
