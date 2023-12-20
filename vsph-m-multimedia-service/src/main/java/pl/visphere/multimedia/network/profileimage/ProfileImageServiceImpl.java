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
import pl.visphere.lib.StringParser;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.GenericRestException;
import pl.visphere.lib.file.MimeType;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.cache.CacheName;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileEntity;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileRepository;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.FileException;
import pl.visphere.multimedia.exception.GuildProfileException;
import pl.visphere.multimedia.file.FileHelper;
import pl.visphere.multimedia.i18n.LocaleSet;
import pl.visphere.multimedia.network.profileimage.dto.GuildProfileImageDetailsResDto;
import pl.visphere.multimedia.network.profileimage.dto.ProfileImageDetailsResDto;
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
    private final CacheService cacheService;

    private final AccountProfileRepository accountProfileRepository;
    private final GuildProfileRepository guildProfileRepository;

    @Override
    public ProfileImageDetailsResDto getProfileImageDetails(AuthUserDetails user) {
        final AccountProfileEntity accountProfile = cacheService
            .getSafetyFromCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, user.getId(),
                AccountProfileEntity.class, () -> accountProfileRepository.findByUserId(user.getId()))
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));
        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .imageType(accountProfile.getImageType().getType())
            .build();
        log.info("Successfully generate profile image details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public GuildProfileImageDetailsResDto getGuildProfileImageDetails(long guildId, AuthUserDetails user) {
        final GuildProfileEntity guildProfile = cacheService
            .getSafetyFromCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildId,
                GuildProfileEntity.class, () -> guildProfileRepository.findByGuildId(guildId))
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(guildId));

        final String resourcePath = s3Client
            .createFullResourcePath(S3Bucket.SPHERES, guildId, new FilePayload(), guildProfile.getProfileImageUuid());

        final GuildDetailsReqDto guildDetailsReqDto = GuildDetailsReqDto.builder()
            .guildId(guildId)
            .isModifiable(false)
            .build();

        final GuildDetailsResDto detailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_DETAILS, guildDetailsReqDto, GuildDetailsResDto.class);

        final GuildProfileImageDetailsResDto resDto = GuildProfileImageDetailsResDto.builder()
            .guildName(detailsResDto.name())
            .createdDate(detailsResDto.createdDate())
            .profileColor(guildProfile.getProfileColor())
            .profileImageUrl(resourcePath)
            .imageType(guildProfile.getImageType().getType())
            .build();

        log.info("Successfully generate guild profile image details: '{}' for guild ID: '{}'.", resDto, guildId);
        return resDto;
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto uploadProfileImage(MultipartFile image, AuthUserDetails user) {
        validateUploadedImage(image);

        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(user.getId()));

        final ObjectData objectData = scaledAndSaveImageOnS3(S3Bucket.USERS, image, user.getId());
        accountProfile.setProfileImageUuid(objectData.uuid());
        accountProfile.setImageType(ImageType.CUSTOM);

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, user.getId());

        log.info("Successfully scaled and saved user image with path: '{}'.", objectData.fullPath());
        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS))
            .resourcePath(objectData.fullPath())
            .build();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto uploadGuildProfileImage(long guildId, MultipartFile image, AuthUserDetails user) {
        validateUploadedImage(image);

        final GuildProfileEntity guildProfile = guildProfileRepository
            .findByGuildId(guildId)
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(guildId));

        final GuildDetailsReqDto guildDetailsReqDto = GuildDetailsReqDto.builder()
            .guildId(guildId)
            .loggedUserId(user.getId())
            .isModifiable(true)
            .build();

        syncQueueHandler.sendNotNullWithBlockThread(QueueTopic.GET_GUILD_DETAILS,
            guildDetailsReqDto, GuildDetailsResDto.class);

        final ObjectData objectData = scaledAndSaveImageOnS3(S3Bucket.SPHERES, image, guildId);
        guildProfile.setProfileImageUuid(objectData.uuid());
        guildProfile.setImageType(ImageType.CUSTOM);

        cacheService.deleteCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildProfile.getGuildId());

        log.info("successfully scaled and saved guild image with path: '{}'.", objectData.fullPath());
        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.GUILD_PROFILE_CUSTOM_IMAGE_UPDATE_RESPONSE_SUCCESS))
            .resourcePath(objectData.fullPath())
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

        final ObjectData res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);
        accountProfile.setProfileImageUuid(res.uuid());
        accountProfile.setImageType(ImageType.IDENTICON);

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, user.getId());

        log.info("Successfully generated identicon image for username: '{}' and color: '{}'.", user.getUsername(),
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

        final FilePayload filePayload = new FilePayload(initialsDrawer
            .drawImage(initials, accountProfile.getProfileColor()), imageDrawer.getFileExtension());

        s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);
        final ObjectData res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);

        accountProfile.setProfileImageUuid(res.uuid());
        accountProfile.setImageType(ImageType.DEFAULT);

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, user.getId());

        log.info("Successfully removed image and generated initials profile for username: '{}' and color: '{}'.",
            user.getUsername(), accountProfile.getProfileColor());

        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS))
            .resourcePath(res.fullPath())
            .build();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto deleteGuildProfileImage(long guildId, AuthUserDetails user) {
        final GuildProfileEntity guildProfile = guildProfileRepository
            .findByGuildId(guildId)
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(guildId));

        final GuildDetailsReqDto guildDetailsReqDto = GuildDetailsReqDto.builder()
            .guildId(guildId)
            .loggedUserId(user.getId())
            .isModifiable(true)
            .build();

        final GuildDetailsResDto detailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_DETAILS, guildDetailsReqDto, GuildDetailsResDto.class);

        final byte[] imageData = initialsDrawer
            .drawImage(StringParser.parseGuildNameInitials(detailsResDto.name()), guildProfile.getProfileColor());
        final FilePayload filePayload = new FilePayload(imageData, imageDrawer.getFileExtension());

        s3Client.clearObjects(S3Bucket.SPHERES, guildProfile.getGuildId(), S3ResourcePrefix.PROFILE);
        final ObjectData res = s3Client.putObject(S3Bucket.SPHERES, guildProfile.getGuildId(), filePayload);

        guildProfile.setProfileImageUuid(res.uuid());
        guildProfile.setImageType(ImageType.DEFAULT);

        cacheService.deleteCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildProfile.getGuildId());

        log.info("Successfully removed image and generated initials profile for guild with ID: '{}'.", guildId);
        return MessageWithResourcePathResDto.builder()
            .message(i18nService.getMessage(LocaleSet.GUILD_PROFILE_CUSTOM_IMAGE_DELETE_RESPONSE_SUCCESS))
            .resourcePath(res.fullPath())
            .build();
    }

    private void validateUploadedImage(MultipartFile image) {
        final MimeType[] supportedExt = { MimeType.PNG, MimeType.JPEG, MimeType.JPG };
        if (!fileHelper.checkIfExtensionIsSupported(image, supportedExt)) {
            throw new FileException.FileUnsupportedExtensionException(supportedExt);
        }
        final int maxMb = imageProperties.getMaxSizeMb();
        if (fileHelper.checkIfExceededMaxSize(image, maxMb)) {
            throw new FileException.FileExceededMaxSizeException(fileHelper.mbFormat(maxMb), image.getSize());
        }
    }

    private ObjectData scaledAndSaveImageOnS3(S3Bucket bucket, MultipartFile image, Long resourceId) {
        ObjectData objectData;
        try {
            final byte[] scaledImageData = imageDrawer.drawImage(image.getBytes(), StringUtils.EMPTY);
            s3Client.clearObjects(bucket, resourceId, S3ResourcePrefix.PROFILE);

            final FilePayload filePayload = new FilePayload(scaledImageData, imageDrawer.getFileExtension());
            objectData = s3Client.putObject(bucket, resourceId, filePayload);
        } catch (IOException ex) {
            throw new GenericRestException("Unable to scale and save image. Cause: '{}'.", ex.getMessage());
        }
        return objectData;
    }
}
