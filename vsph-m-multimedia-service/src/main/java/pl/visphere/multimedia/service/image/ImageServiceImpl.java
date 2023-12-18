/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.StringParser;
import pl.visphere.lib.kafka.payload.multimedia.*;
import pl.visphere.lib.s3.*;
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

import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final InitialsDrawer initialsDrawer;
    private final LockerDrawer lockerDrawer;
    private final IdenticonDrawer identiconDrawer;
    private final S3Client s3Client;
    private final S3Helper s3Helper;

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

        final AccountProfileEntity savedGuildProfile = accountProfileRepository.save(accountProfile);

        log.info("Successfully updated default user profile image: '{}'.", savedGuildProfile);
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

        log.info("Successfully generated default guild profile image: '{}'.", savedGuildProfile);
        return DefaultGuildProfileResDto.builder()
            .imageFullPath(res.fullPath())
            .build();
    }

    @Override
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

        final GuildProfileEntity savedGuildProfile = guildProfileRepository.save(guildProfile);

        log.info("Successfully updated default guild profile image: '{}'.", savedGuildProfile);
        return DefaultGuildProfileResDto.builder()
            .imageFullPath(res.fullPath())
            .build();
    }

    @Override
    public ProfileImageDetailsResDto getProfileImageDetails(Long userId) {
        final AccountProfileEntity accountProfile = accountProfileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountProfileException.AccountProfileNotFoundException(userId));

        final ProfileImageDetailsResDto resDto = ProfileImageDetailsResDto.builder()
            .profileColor(accountProfile.getProfileColor())
            .profileImageUuid(accountProfile.getProfileImageUuid())
            .profileImagePath(s3Helper.prepareUserProfilePath(userId, accountProfile.getProfileImageUuid()))
            .build();

        log.info("Successfully get account profile details: '{}'.", resDto);
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
}
