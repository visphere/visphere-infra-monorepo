/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.StringParser;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.*;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetails;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetailsReqDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2UsersDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.*;
import pl.visphere.multimedia.cache.CacheName;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileEntity;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileRepository;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.GuildProfileException;
import pl.visphere.multimedia.processing.drawer.IdenticonDrawer;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;
import pl.visphere.multimedia.processing.drawer.LockerDrawer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final InitialsDrawer initialsDrawer;
    private final LockerDrawer lockerDrawer;
    private final IdenticonDrawer identiconDrawer;
    private final S3Client s3Client;
    private final S3Helper s3Helper;
    private final CacheService cacheService;
    private final SyncQueueHandler syncQueueHandler;

    private final AccountProfileRepository accountProfileRepository;
    private final GuildProfileRepository guildProfileRepository;

    @Override
    public ProfileImageDetailsResDto generateDefaultProfile(DefaultUserProfileReqDto reqDto) {
        final String randomColor = initialsDrawer.getRandomColor();
        final byte[] imageData = initialsDrawer.drawImage(reqDto.initials(), randomColor);

        final ObjectData res = s3Client.putObject(S3Bucket.USERS, reqDto.userId(), new FilePayload(imageData));

        final AccountProfileEntity accountProfile = AccountProfileEntity.builder()
            .profileColor(randomColor)
            .profileImageUuid(res.uuid())
            .imageType(ImageType.DEFAULT)
            .userId(reqDto.userId())
            .build();

        accountProfileRepository.save(accountProfile);

        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .profileImageUuid(res.uuid())
            .profileImagePath(res.fullPath())
            .profileColor(randomColor)
            .build();

        log.info("Successfully generated default user profile image: '{}'.", resDto);
        return resDto;
    }

    @Override
    @Transactional
    public ProfileImageDetailsResDto updateDefaultProfile(UpdateUserProfileReqDto reqDto) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(reqDto.userId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(reqDto.userId()));

        if (accountProfile.getImageType().equals(ImageType.CUSTOM)) {
            final FilePayload filePayload = FilePayload.builder()
                .prefix(S3ResourcePrefix.PROFILE)
                .extension(FileExtension.PNG)
                .build();

            log.info("Detect not default image. Skipping updating.");
            return ProfileImageDetailsResDto.builder()
                .profileImageUuid(accountProfile.getProfileImageUuid())
                .profileImagePath(s3Client.createFullResourcePath(S3Bucket.USERS, reqDto.userId(), filePayload,
                    accountProfile.getProfileImageUuid()))
                .build();
        }
        byte[] imageData = switch (accountProfile.getImageType()) {
            case DEFAULT -> initialsDrawer.drawImage(reqDto.initials(), accountProfile.getProfileColor());
            case IDENTICON -> identiconDrawer.drawImage(reqDto.username(), accountProfile.getProfileColor());
            case CUSTOM -> new byte[0];
        };
        s3Client.clearObjects(S3Bucket.USERS, reqDto.userId(), S3ResourcePrefix.PROFILE);
        final ObjectData res = s3Client.putObject(S3Bucket.USERS, reqDto.userId(), new FilePayload(imageData));

        accountProfile.setProfileImageUuid(res.uuid());

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, reqDto.userId());
        log.info("Successfully updated default user profile image: '{}'.", res);
        return ProfileImageDetailsResDto.builder()
            .profileImageUuid(res.uuid())
            .profileImagePath(res.fullPath())
            .build();
    }

    @Override
    @Transactional
    public void replaceProfileWithLocked(Long userId) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(userId));

        final byte[] imageData = lockerDrawer.drawImage(accountProfile.getProfileColor(), null);

        final String key = s3Client.findObjectKey(S3Bucket.USERS, userId, S3ResourcePrefix.PROFILE);
        s3Client.moveObject(S3Bucket.USERS, S3Bucket.LOCKED_USERS, key);

        final ObjectData res = s3Client.putObject(S3Bucket.USERS, userId, new FilePayload(imageData));
        accountProfile.setProfileImageUuid(res.uuid());

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, userId);
        log.info("Successfully replaced profile image with locked profile: '{}'.", accountProfile);
    }

    @Override
    @Transactional
    public void replaceLockedWithProfile(Long userId) {
        final String key = s3Client.findObjectKey(S3Bucket.LOCKED_USERS, userId, S3ResourcePrefix.PROFILE);
        final ObjectData res = s3Client.parseObjectKey(S3Bucket.LOCKED_USERS, key);

        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(userId));

        final StringJoiner resourceName = new StringJoiner("-")
            .add(S3ResourcePrefix.PROFILE.getPrefix())
            .add(accountProfile.getProfileImageUuid() + "." + FileExtension.PNG.getExt());

        final StringJoiner lockedKey = new StringJoiner("/")
            .add(userId.toString())
            .add(resourceName.toString());

        s3Client.deleteObject(S3Bucket.USERS, lockedKey.toString());
        s3Client.moveObject(S3Bucket.LOCKED_USERS, S3Bucket.USERS, key);

        accountProfile.setProfileImageUuid(res.uuid());

        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, userId);
        log.info("Successfully revert locked profile with previous user setting: '{}'.", accountProfile);
    }

    @Override
    public DefaultGuildProfileResDto generateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto) {
        final String randomColor = initialsDrawer.getRandomColor();
        final byte[] imageData = initialsDrawer
            .drawImage(StringParser.parseGuildNameInitials(reqDto.guildName()), randomColor);

        final ObjectData res = s3Client
            .putObject(S3Bucket.SPHERES, reqDto.guildId(), new FilePayload(imageData));

        final GuildProfileEntity guildProfile = GuildProfileEntity.builder()
            .profileColor(randomColor)
            .profileImageUuid(res.uuid())
            .imageType(ImageType.DEFAULT)
            .guildId(reqDto.guildId())
            .build();

        final GuildProfileEntity savedGuildProfile = guildProfileRepository.save(guildProfile);
        cacheService.deleteCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildProfile.getGuildId());

        log.info("Successfully generated default guild profile image: '{}'.", savedGuildProfile);
        return DefaultGuildProfileResDto.builder()
            .imageFullPath(res.fullPath())
            .build();
    }

    @Override
    @Transactional
    public DefaultGuildProfileResDto updateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto) {
        final GuildProfileEntity guildProfile = guildProfileRepository
            .findByGuildId(reqDto.guildId())
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(reqDto.guildId()));

        if (!guildProfile.getImageType().equals(ImageType.DEFAULT)) {
            final FilePayload filePayload = FilePayload.builder()
                .prefix(S3ResourcePrefix.PROFILE)
                .extension(FileExtension.PNG)
                .build();

            log.info("Detect not default image. Skipping updating.");
            return DefaultGuildProfileResDto.builder()
                .imageFullPath(s3Client.createFullResourcePath(S3Bucket.SPHERES, reqDto.guildId(), filePayload,
                    guildProfile.getProfileImageUuid()))
                .build();
        }
        final byte[] imageData = initialsDrawer
            .drawImage(StringParser.parseGuildNameInitials(reqDto.guildName()), guildProfile.getProfileColor());

        s3Client.clearObjects(S3Bucket.SPHERES, reqDto.guildId(), S3ResourcePrefix.PROFILE);
        final ObjectData res = s3Client
            .putObject(S3Bucket.SPHERES, reqDto.guildId(), new FilePayload(imageData));

        guildProfile.setProfileImageUuid(res.uuid());
        cacheService.deleteCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildProfile.getGuildId());

        log.info("Successfully updated default guild profile image: '{}'.", res);
        return DefaultGuildProfileResDto.builder()
            .imageFullPath(res.fullPath())
            .build();
    }

    @Override
    public ProfileImageDetailsResDto getProfileImageDetails(ProfileImageDetailsReqDto reqDto) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(reqDto.userId())
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(reqDto.userId()));

        String supplier = "local";
        boolean isCustomImage = true;
        String profileImagePath = s3Helper
            .prepareUserProfilePath(reqDto.userId(), accountProfile.getProfileImageUuid());

        if (reqDto.isExternalCredentialsSupplier()) {
            final OAuth2DetailsResDto detailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_DETAILS, reqDto.userId(), OAuth2DetailsResDto.class);
            if (detailsResDto.profileImageSuppliedByProvider()) {
                profileImagePath = detailsResDto.profileImageUrl();
                isCustomImage = false;
            }
            supplier = detailsResDto.supplier();
        }
        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .profileColor(accountProfile.getProfileColor())
            .profileImageUuid(accountProfile.getProfileImageUuid())
            .profileImagePath(profileImagePath)
            .credentialsSupplier(supplier)
            .isCustomImage(isCustomImage)
            .build();

        log.info("Successfully get account profile details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public ProfileImageDetailsResDto getGuildProfileImageDetails(Long guildId) {
        final GuildProfileEntity guildProfile = guildProfileRepository
            .findByGuildId(guildId)
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(guildId));

        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .profileColor(guildProfile.getProfileColor())
            .profileImageUuid(guildProfile.getProfileImageUuid())
            .profileImagePath(s3Helper.prepareGuildProfilePath(guildId, guildProfile.getProfileImageUuid()))
            .build();

        log.info("Successfully get guild profile details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public GuildImageByIdsResDto getGuildImagesByGuildIds(GuildImageByIdsReqDto reqDto) {
        final List<GuildProfileEntity> guilds = guildProfileRepository.findAllByGuildIdIn(reqDto.guildIds());
        log.info("Successfully get guild profile images: '{}' by guild ids: '{}'", guilds.size(), reqDto.guildIds());

        final List<GuildImageData> resDtos = guilds.stream()
            .map(guild -> GuildImageData.builder()
                .imageUrl(s3Client.createFullResourcePath(S3Bucket.SPHERES, guild.getGuildId(), new FilePayload(),
                    guild.getProfileImageUuid()))
                .guildId(guild.getGuildId())
                .build())
            .toList();

        return new GuildImageByIdsResDto(resDtos);
    }

    @Override
    public UsersImagesDetailsResDto getUsersImagesDetails(UsersImagesDetailsReqDto reqDto) {
        final List<Long> activeLocalUserIds = determinateUserIds(reqDto,
            details -> !details.accountDeleted() && !details.externalSupplier());

        final List<Long> deletedAccountIds = determinateUserIds(reqDto, UserImagesIdentify::accountDeleted);
        final List<Long> oauth2UserIds = determinateUserIds(reqDto, UserImagesIdentify::externalSupplier);

        final Map<Long, OAuth2UsersDetails> oauth2UsersImages = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_USERS_DETAILS,
                new OAuth2UsersDetailsReqDto(oauth2UserIds), OAuth2UsersDetailsResDto.class)
            .oauth2UsersImages();

        final List<Map.Entry<Long, OAuth2UsersDetails>> oauth2ExternalImages = oauth2UsersImages.entrySet().stream()
            .filter(oAuth2UsersDetails -> !oAuth2UsersDetails.getValue().imageFromLocal())
            .toList();

        final List<Long> activeUserIds = Stream.concat(oauth2UsersImages.entrySet().stream()
            .filter(entry -> entry.getValue().imageFromLocal())
            .map(Map.Entry::getKey), activeLocalUserIds.stream()).toList();

        final int size = reqDto.userImagesIdentifies().size();
        final List<AccountProfileEntity> accountProfiles = accountProfileRepository
            .findAllByUserIdIn(activeUserIds);

        final Map<Long, String> userImagesDetails = new HashMap<>(size);
        for (final AccountProfileEntity accountProfile : accountProfiles) {
            final Long userId = accountProfile.getUserId();
            final String resourceUrl = s3Client.createFullResourcePath(S3Bucket.USERS, userId, new FilePayload(),
                accountProfile.getProfileImageUuid());
            userImagesDetails.put(userId, resourceUrl);
        }
        for (final Long userId : deletedAccountIds) {
            userImagesDetails.put(userId, StringUtils.EMPTY);
        }
        for (final Map.Entry<Long, OAuth2UsersDetails> oauth2ExternalImage : oauth2ExternalImages) {
            userImagesDetails.put(oauth2ExternalImage.getKey(), oauth2ExternalImage.getValue().profileImageUrl());
        }
        log.info("Successfully processed images details for: '{}' accounts (deleted: '{}').",
            size, deletedAccountIds.size());
        return new UsersImagesDetailsResDto(userImagesDetails);
    }

    @Override
    @Transactional
    public void deleteUserImageData(Long userId) {
        accountProfileRepository.deleteByUserId(userId);
        s3Client.clearObjects(S3Bucket.USERS, userId, S3ResourcePrefix.PROFILE);
        log.info("Successfully deleted user with ID: '{}' image data details and resources.", userId);
    }

    @Override
    @Transactional
    public void deleteGuildImageData(Long guildId) {
        guildProfileRepository.deleteByGuildId(guildId);
        s3Client.clearObjects(S3Bucket.SPHERES, guildId, S3ResourcePrefix.PROFILE);
        log.info("Successfully deleted Sphere guild with ID: '{}' image data details and resources.", guildId);
    }

    private List<Long> determinateUserIds(UsersImagesDetailsReqDto reqDto, Predicate<UserImagesIdentify> predicate) {
        return reqDto.userImagesIdentifies().stream()
            .filter(predicate)
            .map(UserImagesIdentify::userId)
            .toList();
    }
}
