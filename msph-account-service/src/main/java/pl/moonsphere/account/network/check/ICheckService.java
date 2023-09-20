/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import pl.moonsphere.account.network.check.dto.CheckAlreadyExistResDto;
import pl.moonsphere.account.network.check.dto.MyAccountReqDto;
import pl.moonsphere.account.network.check.dto.MyAccountResDto;

import java.util.List;

interface ICheckService {
    CheckAlreadyExistResDto checkIfAccountValueAlreadyExist(AccountValueParam by, String value);
    List<MyAccountResDto> checkIfMyAccountsExists(List<MyAccountReqDto> reqDtos);
}
