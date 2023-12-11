/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.mfa;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendStateEmailReqDto;

@Component
@RequiredArgsConstructor
class MfaMapper {
    private final ModelMapper modelMapper;

    public SendStateEmailReqDto mapToSendStateEmailReq(UserEntity user, boolean isStateActive) {
        final SendStateEmailReqDto reqDto = modelMapper.map(user, SendStateEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setFullName(createFullName(user));
        reqDto.setIsStateActive(isStateActive);
        reqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        return reqDto;
    }

    public SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user) {
        final SendBaseEmailReqDto reqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setFullName(createFullName(user));
        reqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        return reqDto;
    }

    private String createFullName(UserEntity user) {
        return user.getFirstName() + StringUtils.SPACE + user.getLastName();
    }
}
