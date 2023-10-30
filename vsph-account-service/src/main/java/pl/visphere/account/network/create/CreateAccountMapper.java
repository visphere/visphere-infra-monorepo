/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;
import pl.visphere.lib.kafka.payload.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.auth.CreateUserResDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
class CreateAccountMapper {
    private final ModelMapper modelMapper;

    AccountEntity mapToAccountEntity(CreateAccountReqDto reqDto, Long userId) {
        final AccountEntity account = modelMapper.map(reqDto, AccountEntity.class);
        account.setBirthDate(LocalDate.parse(reqDto.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        account.setUserId(userId);
        return account;
    }

    SendTokenEmailReqDto mapToSendTokenEmailReq(CreateAccountReqDto reqDto, CreateUserResDto resDto) {
        final SendTokenEmailReqDto emailReqDto = modelMapper.map(reqDto, SendTokenEmailReqDto.class);
        emailReqDto.setFullName(reqDto.getFirstName() + StringUtils.SPACE + reqDto.getLastName());
        emailReqDto.setOtaToken(resDto.token());
        emailReqDto.setExpiredAt(resDto.expiredAt());
        return emailReqDto;
    }
}
