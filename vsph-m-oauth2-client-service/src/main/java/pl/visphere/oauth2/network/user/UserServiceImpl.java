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
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;
import pl.visphere.lib.kafka.payload.user.LoginOAuth2UserDetailsResDto;
import pl.visphere.lib.kafka.payload.user.UpdateOAuth2UserDetailsReqDto;
import pl.visphere.lib.kafka.payload.user.UserDetailsResDto;
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
    private final AsyncQueueHandler asyncQueueHandler;

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

        log.info("Successfully parsed and created OAuth2 user initial data: '{}'.", resDto);
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

        final DefaultUserProfileReqDto profileReqDto = DefaultUserProfileReqDto.builder()
            .initials(new char[]{ reqDto.getFirstName().charAt(0), reqDto.getLastName().charAt(0) })
            .userId(oAuth2User.getUserId())
            .build();

        final ProfileImageDetailsResDto profileResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto,
                ProfileImageDetailsResDto.class);

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.INSTANTIATE_USER_RELATED_SETTINGS,
            oAuth2User.getUserId());

        final LoginResDto resDto = modelMapper.map(loginResDto, LoginResDto.class);
        resDto.setId(oAuth2User.getUserId());
        resDto.setProfileUrl(oAuth2User.getProfileImageUrl());
        resDto.setProfileColor(profileResDto.getProfileColor());
        resDto.setSettings(new UserSettingsResDto());
        resDto.setIsDisabled(loginResDto.isDisabled());
        resDto.setJoinDate(loginResDto.getJoinDate());
        resDto.setCredentialsSupplier(oAuth2User.getSupplier().getSupplierName());
        resDto.setImageFromExternalProvider(oAuth2User.getProviderImageSelected());

        final SendBaseEmailReqDto emailReqDto = SendBaseEmailReqDto.builder()
            .userId(oAuth2User.getUserId())
            .username(loginResDto.getUsername())
            .fullName(userDetailsReqDto.getFirstName() + StringUtils.SPACE + userDetailsReqDto.getLastName())
            .emailAddress(loginResDto.getEmailAddress())
            .isExternalCredentialsSupplier(true)
            .build();
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_NEW_ACCOUNT, emailReqDto);

        log.info("Successfully updated and init logged OAuth2 user with data: '{}'.", resDto);
        return resDto;
    }

    @Override
    public LoginResDto loginViaProvider(String token) {
        final OAuth2UserEntity oAuth2User = checkTokenAndExtractUser(token);

        final LoginOAuth2UserDetailsResDto loginResDto = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.LOGIN_OAUTH2_USER, oAuth2User.getUserId(), LoginOAuth2UserDetailsResDto.class);

        final ProfileImageDetailsReqDto reqDto = ProfileImageDetailsReqDto.builder()
            .userId(oAuth2User.getUserId())
            .isExternalCredentialsSupplier(true)
            .build();

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, reqDto, ProfileImageDetailsResDto.class);

        final UserSettingsResDto settingsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USER_PERSISTED_RELATED_SETTINGS, oAuth2User.getUserId(),
                UserSettingsResDto.class);

        final LoginResDto resDto = modelMapper.map(loginResDto, LoginResDto.class);
        final String imageUrl = oAuth2User.getProviderImageSelected()
            ? oAuth2User.getProfileImageUrl()
            : profileImageDetails.getProfileImagePath();

        resDto.setId(oAuth2User.getUserId());
        resDto.setSettings(settingsResDto);
        resDto.setProfileUrl(imageUrl);
        resDto.setProfileColor(profileImageDetails.getProfileColor());
        resDto.setIsDisabled(loginResDto.isDisabled());
        resDto.setJoinDate(loginResDto.getJoinDate());
        resDto.setCredentialsSupplier(oAuth2User.getSupplier().getSupplierName());
        resDto.setImageFromExternalProvider(oAuth2User.getProviderImageSelected());

        log.info("Successfully login OAuth2 user with data: '{}'.", resDto);
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
