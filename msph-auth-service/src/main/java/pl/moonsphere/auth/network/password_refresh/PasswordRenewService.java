/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.moonsphere.auth.i18n.LocaleSet;
import pl.moonsphere.auth.network.password_refresh.dto.AttemptReqDto;
import pl.moonsphere.auth.network.password_refresh.dto.ChangeReqDto;
import pl.moonsphere.lib.BaseMessageResDto;
import pl.moonsphere.lib.i18n.I18nService;

@Service
@RequiredArgsConstructor
public class PasswordRefreshService implements IPasswordRefreshService {
    private final I18nService i18nService;

    @Override
    public BaseMessageResDto request(AttemptReqDto reqDto) {
        // next

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ATTEMPT_CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseVerificationResDto verify(String token) {
        // next

        return BaseVerificationResDto.builder()
            .isValid(false)
            .build();
    }

    @Override
    public BaseMessageResDto change(String token, ChangeReqDto reqDto) {
        // next

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CHANGE_PASSWORD_RESPONSE_SUCCESS))
            .build();
    }
}
