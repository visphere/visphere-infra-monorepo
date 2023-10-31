/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.userprofile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.s3.S3Client;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.i18n.LocaleSet;
import pl.visphere.multimedia.network.userprofile.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.processing.drawer.GravatarDrawer;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

@Slf4j
@Service
@RequiredArgsConstructor
class UserProfileServiceImpl implements UserProfileService {
    private final I18nService i18nService;
    private final S3Client s3Client;
    private final GravatarDrawer gravatarDrawer;
    private final InitialsDrawer initialsDrawer;

    @Override
    public MessageWithResourcePathResDto uploadProfileImage(MultipartFile image, AuthUserDetails user) {
        // scale and transform image
        // convert to jpeg
        // save in S3 storage

        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS))
            .resourcePath("123")
            .build();
    }

    @Override
    public MessageWithResourcePathResDto generateGravatarImage(AuthUserDetails user) {
        // generate gravatar
        // replace image with gravatar and save in S3 storage

        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_GRAVATAR_IMAGE_RESPONSE_SUCCESS))
            .resourcePath("")
            .build();
    }

    @Override
    public BaseMessageResDto deleteProfileImage(AuthUserDetails user) {
        // generate basic user image
        // replace image in S3 with basic image and save in S3 storage

        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS))
            .build();
    }
}
