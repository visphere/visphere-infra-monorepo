/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.email;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;

@Component
@RequiredArgsConstructor
class EmailMapper {
    private final ModelMapper modelMapper;

    SendTokenEmailReqDto mapToSendTokenEmailReq(
        UserEntity user, String emailAddress, GenerateOtaResDto otaResDto, ProfileImageDetailsResDto resDto
    ) {
        final SendTokenEmailReqDto reqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setEmailAddress(emailAddress);
        reqDto.setFullName(createFullName(user));
        reqDto.setProfileImageUuid(resDto.profileImageUuid());
        reqDto.setOtaToken(otaResDto.token());
        return reqDto;
    }

    SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user, String emailAddress, ProfileImageDetailsResDto resDto) {
        final SendBaseEmailReqDto reqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        reqDto.setUserId(user.getId());
        reqDto.setEmailAddress(emailAddress);
        reqDto.setFullName(createFullName(user));
        reqDto.setProfileImageUuid(resDto.profileImageUuid());
        return reqDto;
    }

    private String createFullName(UserEntity user) {
        return user.getFirstName() + StringUtils.SPACE + user.getLastName();
    }
}
