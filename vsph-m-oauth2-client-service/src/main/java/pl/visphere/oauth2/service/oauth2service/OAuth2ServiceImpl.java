/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.service.oauth2service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.auth.OAuth2UserDetailsResDto;
import pl.visphere.lib.kafka.payload.auth.PersistOAuth2UserReqDto;
import pl.visphere.lib.kafka.payload.auth.PersistOAuth2UserResDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.oauth2.core.OAuth2Supplier;
import pl.visphere.oauth2.core.user.OAuth2UserInfo;
import pl.visphere.oauth2.core.user.OAuth2UserLoader;
import pl.visphere.oauth2.core.user.OAuth2UserResponse;
import pl.visphere.oauth2.core.user.UserLoaderData;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserEntity;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserRepository;
import pl.visphere.oauth2.exception.OAuth2UserException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service, OAuth2UserLoader {
    private final SyncQueueHandler syncQueueHandler;
    private final ModelMapper modelMapper;

    private final OAuth2UserRepository oAuth2UserRepository;

    @Override
    @Transactional
    public OAuth2UserResponse loadAndAuthUser(UserLoaderData userLoaderData, OAuth2UserInfo userInfo) {
        boolean isAlreadySignup = false;
        AuthUserDetails authUserDetails;

        final OAuth2Supplier supplier = userLoaderData.supplier();
        final Optional<OAuth2UserEntity> oAuth2UserOptional = oAuth2UserRepository
            .findByProviderIdAndSupplier(userInfo.getId(), supplier);

        if (oAuth2UserOptional.isEmpty()) {
            final PersistOAuth2UserReqDto reqDto = modelMapper.map(userInfo, PersistOAuth2UserReqDto.class);
            final PersistOAuth2UserResDto resDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.PERSIST_NEW_USER, reqDto, PersistOAuth2UserResDto.class);

            final OAuth2UserEntity newOAuth2User = OAuth2UserEntity.builder()
                .supplier(supplier)
                .providerId(userInfo.getId())
                .profileImageUrl(userInfo.getUserImageUrl())
                .userId(resDto.userId())
                .build();

            final OAuth2UserEntity savedOAuth2User = oAuth2UserRepository.save(newOAuth2User);
            log.info("Successfully saved OAuth2 user details: '{}'.", savedOAuth2User);

            authUserDetails = AuthUserDetails.builder()
                .id(resDto.userId())
                .username(resDto.username())
                .emailAddress(reqDto.getEmailAddress())
                .authorities(resDto.authorities())
                .build();
        } else {
            final OAuth2UserEntity oAuth2User = oAuth2UserOptional.get();
            final Long userId = oAuth2User.getUserId();

            final OAuth2UserDetailsResDto resDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_USER_DETAILS, userId, OAuth2UserDetailsResDto.class);

            oAuth2User.setProfileImageUrl(userInfo.getUserImageUrl());
            isAlreadySignup = resDto.isActivated();

            authUserDetails = AuthUserDetails.builder()
                .id(userId)
                .username(resDto.username())
                .emailAddress(resDto.emailAddress())
                .authorities(resDto.authorities())
                .build();
        }
        return OAuth2UserResponse.builder()
            .userDetails(authUserDetails)
            .isAlreadySignup(isAlreadySignup)
            .build();
    }

    @Override
    public OAuth2DetailsResDto getOAuthDetails(Long userId) {
        final OAuth2UserEntity oAuth2User = oAuth2UserRepository
            .findByUserId(userId)
            .orElseThrow(() -> new OAuth2UserException.OAuth2UserNotFoundException(userId));

        final OAuth2DetailsResDto resDto = OAuth2DetailsResDto.builder()
            .supplier(oAuth2User.getSupplier().getSupplierName())
            .profileImageSuppliedByProvider(oAuth2User.getProviderImageSelected())
            .profileImageUrl(oAuth2User.getProfileImageUrl())
            .build();

        log.info("Successfully get and process OAuth2 user account details: '{}'.", resDto);
        return resDto;
    }

    @Override
    @Transactional
    public void deleteOAuth2UserData(Long userId) {
        oAuth2UserRepository.deleteByUserId(userId);
        log.info("Successfully deleted OAuth2 user correlation for user with ID: '{}'.", userId);
    }
}
