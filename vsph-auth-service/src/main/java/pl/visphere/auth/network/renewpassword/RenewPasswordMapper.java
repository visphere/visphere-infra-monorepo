/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.renewpassword;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

@Component
@RequiredArgsConstructor
class RenewPasswordMapper {
    private final ModelMapper modelMapper;

    SendTokenEmailReqDto mapToSendTokenEmailReq(UserEntity user, GenerateOtaResDto otaResDto) {
        final SendTokenEmailReqDto reqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        reqDto.setFullName(createFullName(user));
        reqDto.setOtaToken(otaResDto.token());
        reqDto.setExpiredAt(otaResDto.expiredAt());
        return reqDto;
    }

    SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user) {
        final SendBaseEmailReqDto reqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        reqDto.setFullName(createFullName(user));
        return reqDto;
    }

    private String createFullName(UserEntity user) {
        return user.getFirstName() + StringUtils.SPACE + user.getLastName();
    }
}
