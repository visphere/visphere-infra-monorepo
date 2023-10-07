/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.check;

import pl.visphere.account.network.check.dto.CheckAlreadyExistResDto;
import pl.visphere.account.network.check.dto.MyAccountReqDto;
import pl.visphere.account.network.check.dto.MyAccountResDto;

import java.util.List;

interface ICheckService {
    CheckAlreadyExistResDto checkIfAccountValueAlreadyExist(AccountValueParam by, String value);
    List<MyAccountResDto> checkIfMyAccountsExists(List<MyAccountReqDto> reqDtos);
}
