/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network;

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
public class OtaTokenEmailMapper {
    private final ModelMapper modelMapper;

    public SendTokenEmailReqDto mapToSendTokenEmailReq(UserEntity user, GenerateOtaResDto otaResDto) {
        final SendTokenEmailReqDto reqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setFullName(createFullName(user));
        reqDto.setOtaToken(otaResDto.token());
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
