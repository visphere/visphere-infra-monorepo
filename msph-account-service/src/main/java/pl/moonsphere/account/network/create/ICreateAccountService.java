/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.create;

import pl.moonsphere.account.network.create.dto.ActivateAccountReqDto;
import pl.moonsphere.account.network.create.dto.ActivateAccountResDto;
import pl.moonsphere.account.network.create.dto.CreateAccountReqDto;
import pl.moonsphere.lib.BaseMessageResDto;

interface ICreateAccountService {
    BaseMessageResDto createNew(CreateAccountReqDto reqDto);
    ActivateAccountResDto activate(String token, ActivateAccountReqDto reqDto);
    BaseMessageResDto resend(ActivateAccountReqDto reqDto);
}
