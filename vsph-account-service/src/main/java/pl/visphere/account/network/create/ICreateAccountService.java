/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import pl.visphere.account.network.create.dto.ActivateAccountReqDto;
import pl.visphere.account.network.create.dto.ActivateAccountResDto;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.BaseMessageResDto;

interface ICreateAccountService {
    BaseMessageResDto createNew(CreateAccountReqDto reqDto);
    ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto);
    BaseMessageResDto resend(ActivateAccountReqDto reqDto);
}
