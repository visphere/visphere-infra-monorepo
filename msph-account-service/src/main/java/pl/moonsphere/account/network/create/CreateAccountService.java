/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.moonsphere.account.i18n.LocaleSet;
import pl.moonsphere.account.network.create.dto.ActivateAccountReqDto;
import pl.moonsphere.account.network.create.dto.ActivateAccountResDto;
import pl.moonsphere.account.network.create.dto.CreateAccountReqDto;
import pl.moonsphere.lib.dto.BaseMessageResDto;
import pl.moonsphere.lib.i18n.I18nService;

@Service
@RequiredArgsConstructor
class CreateAccountService implements ICreateAccountService {
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
}
