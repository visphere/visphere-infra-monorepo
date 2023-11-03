/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profilecolor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;
import pl.visphere.lib.s3.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.ColorException;
import pl.visphere.multimedia.i18n.LocaleSet;
import pl.visphere.multimedia.network.profilecolor.dto.UpdateProfileColorReqDto;
import pl.visphere.multimedia.processing.ImageProperties;
import pl.visphere.multimedia.processing.drawer.IdenticonDrawer;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileColorServiceImpl implements ProfileColorService {
    private final S3Client s3Client;
    private final IdenticonDrawer identiconDrawer;
    private final InitialsDrawer initialsDrawer;
    private final SyncQueueHandler syncQueueHandler;
    private final I18nService i18nService;
    private final ImageProperties imageProperties;

    private final AccountProfileRepository accountProfileRepository;

    @Override
    public List<String> getColors() {
        return imageProperties.getColors();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto updateProfileColor(UpdateProfileColorReqDto reqDto, AuthUserDetails user) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));

        final String prevColor = accountProfile.getProfileColor();

        final UserDetailsResDto userDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, user.getId(), UserDetailsResDto.class);

        byte[] updatedImage;
        switch (accountProfile.getImageType()) {
            case DEFAULT -> {
                final char[] initials = {
                    userDetailsResDto.getFirstName().charAt(0),
                    userDetailsResDto.getLastName().charAt(0)
                };
                updatedImage = initialsDrawer.drawImage(initials, reqDto.getColor());
            }
            case IDENTICON -> updatedImage = identiconDrawer
                .drawImage(user.getUsername(), reqDto.getColor());
            default -> throw new ColorException.ColorUpdateNotAvailableException(ImageType.CUSTOM);
        }

        s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);

        final FilePayload filePayload = FilePayload.builder()
            .prefix(S3ResourcePrefix.PROFILE)
            .data(updatedImage)
            .extension(identiconDrawer.getFileExtension())
            .build();

        final InsertedObjectRes res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);
        accountProfile.setProfileImageUuid(res.uuid());
        accountProfile.setProfileColor(reqDto.getColor());

        log.info("Successfully updated profile color from: '{}' to: '{}'", prevColor, reqDto.getColor());
        return MessageWithResourcePathResDto.builder()
            .resourcePath(res.fullPath())
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_COLOR_UPDATE_RESPONSE_SUCCESS))
            .build();
    }
}
