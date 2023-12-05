/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.user;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.*;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.auth.LoginOAuth2UserDetailsResDto;
import pl.visphere.lib.kafka.payload.auth.UpdateOAuth2UserDetailsReqDto;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserEntity;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserRepository;
import pl.visphere.oauth2.exception.OAuth2UserException;
import pl.visphere.oauth2.i18n.LocaleSet;
import pl.visphere.oauth2.network.user.dto.GetFillDataResDto;
import pl.visphere.oauth2.network.user.dto.LoginResDto;
import pl.visphere.oauth2.network.user.dto.UpdateFillDataReqDto;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final SyncQueueHandler syncQueueHandler;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final I18nService i18nService;

    private final OAuth2UserRepository oAuth2UserRepository;

    @Override
    public GetFillDataResDto getUserData(String token) {
        final OAuth2UserEntity oAuth2User = checkTokenAndExtractUser(token);
        final OAuth2Supplier supplier = oAuth2User.getSupplier();

        final UserDetailsResDto userDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, oAuth2User.getUserId(), UserDetailsResDto.class);

        if (userDetailsResDto.isActivated()) {
            throw new OAuth2UserException.OAuth2UserAlreadyActivatedException(oAuth2User.getProviderId(), supplier);
        }
        final GetFillDataResDto resDto = GetFillDataResDto.builder()
            .firstName(userDetailsResDto.getFirstName())
            .lastName(userDetailsResDto.getLastName())
            .profileImageUrl(oAuth2User.getProfileImageUrl())
            .supplier(oAuth2User.getSupplier().getSupplierName())
            .username(userDetailsResDto.getUsername())
            .message(i18nService.getMessage(LocaleSet.GET_FILL_DATA_RESPONSE_SUCCESS))
            .build();

        log.info("Successfully parsed and created OAuth2 user initial data: '{}'", resDto);
        return resDto;
    }

    @Override
    public LoginResDto fillUserData(UpdateFillDataReqDto reqDto, String token) {
        final OAuth2UserEntity oAuth2User = checkTokenAndExtractUser(token);

        final UpdateOAuth2UserDetailsReqDto userDetailsReqDto = modelMapper
            .map(reqDto, UpdateOAuth2UserDetailsReqDto.class);

        userDetailsReqDto.setUserId(oAuth2User.getUserId());

        final LoginOAuth2UserDetailsResDto loginResDto = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.UPDATE_OAUTH2_USER_DETAILS, userDetailsReqDto, LoginOAuth2UserDetailsResDto.class);

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.INSTANTIATE_USER_RELATED_SETTINGS,
            oAuth2User.getUserId());

        final LoginResDto resDto = modelMapper.map(loginResDto, LoginResDto.class);
        resDto.setProfileUrl(oAuth2User.getProfileImageUrl());
        resDto.setSettings(new UserSettingsResDto());

        // TODO: send welcome email message

        log.info("Successfully updated and init logged OAuth2 user with data: '{}'", resDto);
        return resDto;
    }

    @Override
    public LoginResDto loginViaProvider(String token) {
        final OAuth2UserEntity oAuth2User = checkTokenAndExtractUser(token);

        final LoginOAuth2UserDetailsResDto loginResDto = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.LOGIN_OAUTH2_USER, oAuth2User.getUserId(), LoginOAuth2UserDetailsResDto.class);

        final UserSettingsResDto settingsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USER_PERSISTED_RELATED_SETTINGS, oAuth2User.getUserId(),
                UserSettingsResDto.class);

        final LoginResDto resDto = modelMapper.map(loginResDto, LoginResDto.class);

        String imageUrl = StringUtils.EMPTY;
        if (oAuth2User.getProviderImageSelected()) {
            // image provided by OAuth2 provider, skipping
            imageUrl = oAuth2User.getProfileImageUrl();
        } else {
            // image provided by Visphere, get from S3
        }

        // TODO: check, if profile is getting from provider, otherwise get generated profile from S3

        resDto.setSettings(settingsResDto);
        resDto.setProfileUrl(imageUrl);

        log.info("Successfully login OAuth2 user with data: '{}'", resDto);
        return resDto;
    }

    private OAuth2UserEntity checkTokenAndExtractUser(String token) {
        final JwtValidateState state = jwtService.validate(token);
        if (state.state() != JwtState.VALID) {
            throw new JwtException.JwtGeneralException();
        }
        final Claims claims = state.claims();
        final String userOAuth2Id = claims.getSubject();
        final OAuth2Supplier supplier = OAuth2Supplier.valueOf(jwtService.getClaim(claims, JwtClaim.OAUTH2_SUPPLIER));
        return oAuth2UserRepository
            .findByProviderIdAndSupplier(userOAuth2Id, supplier)
            .orElseThrow(() -> new OAuth2UserException.OAuth2UserNotFoundException(userOAuth2Id, supplier));
    }
}
