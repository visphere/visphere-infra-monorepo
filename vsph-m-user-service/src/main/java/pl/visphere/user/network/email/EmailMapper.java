/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.email;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;

@Component
@RequiredArgsConstructor
class EmailMapper {
    private final ModelMapper modelMapper;

    SendTokenEmailReqDto mapToSendTokenEmailReq(UserEntity user, String emailAddress, GenerateOtaResDto otaResDto) {
        final SendTokenEmailReqDto reqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setEmailAddress(emailAddress);
        reqDto.setFullName(createFullName(user));
        reqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        reqDto.setOtaToken(otaResDto.token());
        return reqDto;
    }

    SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user, String emailAddress) {
        final SendBaseEmailReqDto reqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setEmailAddress(emailAddress);
        reqDto.setFullName(createFullName(user));
        reqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        return reqDto;
    }

    private String createFullName(UserEntity user) {
        return user.getFirstName() + StringUtils.SPACE + user.getLastName();
    }
}
