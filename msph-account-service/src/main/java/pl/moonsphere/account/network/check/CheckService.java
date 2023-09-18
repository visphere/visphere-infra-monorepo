/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.moonsphere.account.network.check.dto.CheckUsernameExist;
import pl.moonsphere.account.network.check.dto.MyAccountReqDto;
import pl.moonsphere.account.network.check.dto.MyAccountResDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckService implements ICheckService {
    @Override
    public CheckUsernameExist checkIfUsernameAlreadyExist(String username) {
        // check if passed username is already declared in system

        return CheckUsernameExist.builder()
            .alreadyExist(true)
            .build();
    }

    @Override
    public List<MyAccountResDto> checkIfMyAccountsExists(List<MyAccountReqDto> reqDtos) {
        // check if all passed accounts with isVerified still exists

        return reqDtos.stream()
            .map(dto -> MyAccountResDto.builder()
                .accountId(dto.getAccountId())
                .usernameOrEmailAddress(dto.getUsernameOrEmailAddress())
                .isVerified(dto.getIsVerified())
                .thumbnailUrl(dto.getIsVerified() ? "https://raw.githubusercontent.com/Milosz08/schedule-management-server/master/_StaticPrivateContent/UserImages/zoqeCUQJ1QMqRBH4dDEB__julnow269.jpg" : StringUtils.EMPTY)
                .build())
            .collect(Collectors.toList());
    }
}
