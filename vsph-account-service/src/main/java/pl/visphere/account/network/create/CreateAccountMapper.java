/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.network.create;

import org.springframework.stereotype.Component;
import pl.visphere.account.domain.account.AccountEntity;
import pl.visphere.account.network.create.dto.CreateAccountReqDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
class CreateAccountMapper {
    AccountEntity mapToAccountEntity(CreateAccountReqDto reqDto, Long userId, String defaultColor) {
        return AccountEntity.builder()
            .firstName(reqDto.getFirstName())
            .lastName(reqDto.getLastName())
            .birthDate(LocalDate.parse(reqDto.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            .defaultColor(defaultColor)
            .allowNotifs(reqDto.getAllowNotifs())
            .userId(userId)
            .build();
    }
}
