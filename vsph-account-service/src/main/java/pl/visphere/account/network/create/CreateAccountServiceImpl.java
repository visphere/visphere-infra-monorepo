/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.visphere.account.i18n.LocaleSet;
import pl.visphere.account.network.create.dto.ActivateAccountReqDto;
import pl.visphere.account.network.create.dto.ActivateAccountResDto;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;

@Service
@RequiredArgsConstructor
class CreateAccountServiceImpl implements CreateAccountService {
    private final I18nService i18nService;

    @Override
    public BaseMessageResDto createNew(CreateAccountReqDto reqDto) {
        // create account

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CREATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto) {
        // activate created account via email message

        return ActivateAccountResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .mfaEnabled(false)
            .build();
    }

    @Override
    public BaseMessageResDto resend(ActivateAccountReqDto reqDto) {
        // resend email message with provided token

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }
}
