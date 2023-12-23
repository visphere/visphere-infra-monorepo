/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.network.userimage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserEntity;
import pl.visphere.oauth2.domain.oauth2user.OAuth2UserRepository;
import pl.visphere.oauth2.exception.OAuth2UserException;
import pl.visphere.oauth2.i18n.LocaleSet;
import pl.visphere.oauth2.network.userimage.dto.ImageProviderResDto;

@Slf4j
@Service
@RequiredArgsConstructor
class UserImageServiceImpl implements UserImageService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;

    private final OAuth2UserRepository oAuth2UserRepository;

    @Override
    @Transactional
    public ImageProviderResDto toggleProviderProfileImage(boolean fromProvider, AuthUserDetails user) {
        final OAuth2UserEntity oAuth2User = oAuth2UserRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new OAuth2UserException.OAuth2UserNotFoundException(user.getId()));

        String profileImagePath = oAuth2User.getProfileImageUrl();
        if (!fromProvider) {
            final ProfileImageDetailsReqDto imageDetailsReqDto = ProfileImageDetailsReqDto.builder()
                .userId(user.getId())
                .isExternalCredentialsSupplier(false)
                .build();

            final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, imageDetailsReqDto,
                    ProfileImageDetailsResDto.class);

            profileImagePath = profileImageDetails.getProfileImagePath();
        }
        oAuth2User.setProviderImageSelected(fromProvider);

        log.info("Successfully revert to provider profile image for user: '{}'.", oAuth2User);
        return ImageProviderResDto.builder()
            .message(i18nService.getMessage(LocaleSet.UPDATE_PROFILE_IMAGE_RESPONSE_SUCCESS))
            .resourcePath(profileImagePath)
            .build();
    }
}
