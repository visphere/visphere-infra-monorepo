/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profileimage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.file.MimeType;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.FileException;
import pl.visphere.multimedia.file.FileHelper;
import pl.visphere.multimedia.i18n.LocaleSet;
import pl.visphere.multimedia.processing.ImageProperties;
import pl.visphere.multimedia.processing.drawer.IdenticonDrawer;
import pl.visphere.multimedia.processing.drawer.ImageDrawer;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
class ProfileImageServiceImpl implements ProfileImageService {
    private final I18nService i18nService;
    private final S3Client s3Client;
    private final IdenticonDrawer identiconDrawer;
    private final InitialsDrawer initialsDrawer;
    private final ImageDrawer imageDrawer;
    private final SyncQueueHandler syncQueueHandler;
    private final FileHelper fileHelper;
    private final ImageProperties imageProperties;

    private final AccountProfileRepository accountProfileRepository;

    @Override
    @Transactional
    public MessageWithResourcePathResDto uploadProfileImage(MultipartFile image, AuthUserDetails user) {
        final MimeType[] supportedExt = { MimeType.PNG, MimeType.JPEG, MimeType.JPG };
        if (!fileHelper.checkIfExtensionIsSupported(image, supportedExt)) {
            throw new FileException.FileUnsupportedExtensionException(supportedExt);
        }
        final int maxMb = imageProperties.getMaxSizeMb();
        if (fileHelper.checkIfExceededMaxSize(image, maxMb)) {
            throw new FileException.FileExceededMaxSizeException(fileHelper.mbFormat(maxMb), image.getSize());
        }

        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));

        String resourcePath;
        try {
            final byte[] scaledImageData = imageDrawer.drawImage(image.getBytes(), StringUtils.EMPTY);
            s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);

            final FilePayload filePayload = FilePayload.builder()
                .prefix(S3ResourcePrefix.PROFILE)
                .data(scaledImageData)
                .extension(imageDrawer.getFileExtension())
                .build();
            final InsertedObjectRes res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);

            resourcePath = res.fullPath();
            accountProfile.setProfileImageUuid(res.uuid());
            accountProfile.setImageType(ImageType.CUSTOM);

        } catch (IOException ex) {
            log.error("Unable to scale and save user profile image. Cause: '{}'", ex.getMessage());
            throw new GenericRestException();
        }
        log.info("successfully scaled and saved user image with path: '{}'", resourcePath);
        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS))
            .resourcePath(resourcePath)
            .build();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto generateIdenticonImage(AuthUserDetails user) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));

        final byte[] identiconImageData = identiconDrawer
            .drawImage(user.getUsername(), accountProfile.getProfileColor());

        s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);

        final FilePayload filePayload = FilePayload.builder()
            .prefix(S3ResourcePrefix.PROFILE)
            .data(identiconImageData)
            .extension(imageDrawer.getFileExtension())
            .build();

        final InsertedObjectRes res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);
        accountProfile.setProfileImageUuid(res.uuid());
        accountProfile.setImageType(ImageType.IDENTICON);

        log.info("Successfully generated identicon image for username: '{}' and color: '{}'", user.getUsername(),
            accountProfile.getProfileColor());

        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_IDENTICON_IMAGE_RESPONSE_SUCCESS))
            .resourcePath(res.fullPath())
            .build();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto deleteProfileImage(AuthUserDetails user) {
        final UserDetailsResDto userDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, user.getId(), UserDetailsResDto.class);

        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));

        final char[] initials = {
            userDetailsResDto.getFirstName().charAt(0),
            userDetailsResDto.getLastName().charAt(0)
        };

        final FilePayload filePayload = FilePayload.builder()
            .prefix(S3ResourcePrefix.PROFILE)
            .data(initialsDrawer.drawImage(initials, accountProfile.getProfileColor()))
            .extension(imageDrawer.getFileExtension())
            .build();

        s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);
        final InsertedObjectRes res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);

        accountProfile.setProfileImageUuid(res.uuid());
        accountProfile.setImageType(ImageType.DEFAULT);

        log.info("Successfully removed image and generated initials profile for username: '{}' and color: '{}'",
            user.getUsername(), accountProfile.getProfileColor());

        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS))
            .resourcePath(res.fullPath())
            .build();
    }
}
