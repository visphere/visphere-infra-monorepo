/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.account;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.user.domain.mfauser.MfaUserEntity;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.network.account.dto.AccountDetailsResDto;
import pl.visphere.user.network.account.dto.CreateAccountReqDto;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;

@Component
@RequiredArgsConstructor
class AccountMapper {
    private final ModelMapper modelMapper;

    SendTokenEmailReqDto mapToSendTokenEmailReq(CreateAccountReqDto reqDto, GenerateOtaResDto resDto, Long userId) {
        final SendTokenEmailReqDto emailReqDto = modelMapper.map(reqDto, SendTokenEmailReqDto.class);
        emailReqDto.setUserId(userId);
        emailReqDto.setFullName(reqDto.getFirstName() + StringUtils.SPACE + reqDto.getLastName());
        emailReqDto.setOtaToken(resDto.token());
        emailReqDto.setIsExternalCredentialsSupplier(false);
        return emailReqDto;
    }

    SendTokenEmailReqDto mapToSendTokenEmailReq(UserEntity user, GenerateOtaResDto resDto, Long userId) {
        final SendTokenEmailReqDto emailReqDto = modelMapper.map(user, SendTokenEmailReqDto.class);
        emailReqDto.setUserId(userId);
        emailReqDto.setFullName(user.getFirstName() + StringUtils.SPACE + user.getLastName());
        emailReqDto.setOtaToken(resDto.token());
        emailReqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        return emailReqDto;
    }

    SendBaseEmailReqDto mapToSendBaseEmailReq(UserEntity user) {
        final SendBaseEmailReqDto emailReqDto = modelMapper.map(user, SendBaseEmailReqDto.class);
        emailReqDto.setFullName(user.getFirstName() + StringUtils.SPACE + user.getLastName());
        emailReqDto.setUserId(user.getId());
        emailReqDto.setIsExternalCredentialsSupplier(user.getExternalCredProvider());
        return emailReqDto;
    }

    AccountDetailsResDto mapToAccountDetailsRes(UserEntity user) {
        final AccountDetailsResDto resDto = modelMapper.map(user, AccountDetailsResDto.class);
        final MfaUserEntity mfaUser = user.getMfaUser();
        resDto.setSecondEmailAddress(user.getSecondEmailAddress() == null ? "-" : user.getSecondEmailAddress());
        resDto.setMfaEnabled(mfaUser != null);
        resDto.setExternalOAuth2Supplier(user.getExternalCredProvider());
        resDto.setMfaSetup(mfaUser != null && mfaUser.getMfaIsSetup() != null && mfaUser.getMfaIsSetup());
        return resDto;
    }
}
