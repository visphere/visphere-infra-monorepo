/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.network.account.dto.CreateAccountReqDto;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

@Component
@RequiredArgsConstructor
class AccountMapper {
    private final ModelMapper modelMapper;

    SendTokenEmailReqDto mapToSendTokenEmailReq(CreateAccountReqDto reqDto, GenerateOtaResDto resDto, Long userId) {
        final SendTokenEmailReqDto emailReqDto = modelMapper.map(reqDto, SendTokenEmailReqDto.class);
        emailReqDto.setUserId(userId);
        emailReqDto.setFullName(reqDto.getFirstName() + StringUtils.SPACE + reqDto.getLastName());
        emailReqDto.setOtaToken(resDto.token());
        emailReqDto.setProfileImageUuid(StringUtils.EMPTY);
        return emailReqDto;
    }

    SendTokenEmailReqDto mapToSendTokenEmailReq(UserEntity user, GenerateOtaResDto resDto, Long userId) {
        final SendTokenEmailReqDto emailReqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        emailReqDto.setUserId(userId);
        emailReqDto.setFullName(user.getFirstName() + StringUtils.SPACE + user.getLastName());
        emailReqDto.setOtaToken(resDto.token());
        emailReqDto.setProfileImageUuid(StringUtils.EMPTY);
        return emailReqDto;
    }

    SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user, ProfileImageDetailsResDto profileResDto) {
        final SendBaseEmailReqDto emailReqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        emailReqDto.setFullName(user.getFirstName() + StringUtils.SPACE + user.getLastName());
        emailReqDto.setUserId(user.getId());
        emailReqDto.setProfileImageUuid(profileResDto.profileImageUuid());
        return emailReqDto;
    }
}
