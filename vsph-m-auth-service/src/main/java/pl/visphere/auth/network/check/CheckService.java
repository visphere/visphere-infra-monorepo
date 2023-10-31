/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.check;

import pl.visphere.auth.network.check.dto.CheckAlreadyExistResDto;
import pl.visphere.auth.network.check.dto.MyAccountReqDto;
import pl.visphere.auth.network.check.dto.MyAccountResDto;

import java.util.List;

interface CheckService {
    CheckAlreadyExistResDto checkIfAccountValueAlreadyExist(AccountValueParam by, String value);
    List<MyAccountResDto> checkIfMyAccountsExists(List<MyAccountReqDto> reqDtos);
}
