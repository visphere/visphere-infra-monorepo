/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import pl.visphere.auth.network.account.dto.ActivateAccountReqDto;
import pl.visphere.auth.network.account.dto.ActivateAccountResDto;
import pl.visphere.auth.network.account.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;

interface AccountService {
    BaseMessageResDto createNew(CreateAccountReqDto reqDto);
    ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto);
    BaseMessageResDto resend(ActivateAccountReqDto reqDto);
}
