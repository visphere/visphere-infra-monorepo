/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileResDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.s3.*;
import pl.visphere.multimedia.domain.ImageType;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileEntity;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileRepository;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.GuildProfileException;
import pl.visphere.multimedia.processing.drawer.InitialsDrawer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final InitialsDrawer initialsDrawer;
    private final S3Client s3Client;
    private final S3Helper s3Helper;

    private final AccountProfileRepository accountProfileRepository;
    private final GuildProfileRepository guildProfileRepository;

    @Override
    public void generateDefaultProfile(DefaultUserProfileReqDto reqDto) {
        final String randomColor = initialsDrawer.getRandomColor();
        final byte[] imageData = initialsDrawer.drawImage(reqDto.initials(), randomColor);

        final FilePayload filePayload = createFilePayload(imageData);
        final InsertedObjectRes res = s3Client
            .putObject(S3Bucket.USERS, reqDto.userId(), filePayload);

        final AccountProfileEntity accountProfile = AccountProfileEntity.builder()
            .profileColor(randomColor)
            .profileImageUuid(res.uuid())
            .imageType(ImageType.DEFAULT)
            .userId(reqDto.userId())
            .build();

        final AccountProfileEntity savedAccountProfile = accountProfileRepository.save(accountProfile);
        log.info("Successfully generated default profile image: '{}'", savedAccountProfile);
    }

    @Override
    public DefaultGuildProfileResDto generateDefaultGuildProfile(DefaultGuildProfileReqDto reqDto) {
        final String randomColor = initialsDrawer.getRandomColor();
        final byte[] imageData = initialsDrawer.drawImage(parseInitials(reqDto), randomColor);

        final InsertedObjectRes res = s3Client
            .putObject(S3Bucket.SPHERES, reqDto.guildId(), createFilePayload(imageData));

        final GuildProfileEntity guildProfile = GuildProfileEntity.builder()
            .profileColor(randomColor)
            .profileImageUuid(res.uuid())
            .imageType(ImageType.DEFAULT)
            .guildId(reqDto.guildId())
            .build();

        final GuildProfileEntity savedGuildProfile = guildProfileRepository.save(guildProfile);

        log.info("Successfully generated default guild profile image: '{}'", savedGuildProfile);
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

        final byte[] imageData = initialsDrawer.drawImage(parseInitials(reqDto), guildProfile.getProfileColor());

        s3Client.clearObjects(S3Bucket.SPHERES, reqDto.guildId(), S3ResourcePrefix.PROFILE);
        final InsertedObjectRes res = s3Client
            .putObject(S3Bucket.SPHERES, reqDto.guildId(), createFilePayload(imageData));

        guildProfile.setProfileImageUuid(res.uuid());

        final GuildProfileEntity savedGuildProfile = guildProfileRepository.save(guildProfile);

        log.info("Successfully updated default guild profile image: '{}'", savedGuildProfile);
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

        log.info("Successfully get account profile details: '{}'", resDto);
        return resDto;
    }

    private FilePayload createFilePayload(byte[] imageData) {
        return FilePayload.builder()
            .prefix(S3ResourcePrefix.PROFILE)
            .data(imageData)
            .extension(FileExtension.PNG)
            .build();
    }

    private char[] parseInitials(DefaultGuildProfileReqDto reqDto) {
        final String[] parts = reqDto.guildName().split(StringUtils.SPACE);
        final char[] initials;
        if (parts.length > 1) {
            initials = new char[2];
            for (int i = 0; i < 2; i++) {
                initials[i] = parts[i].charAt(0);
            }
        } else {
            initials = new char[]{ parts[0].charAt(0) };
        }
        return initials;
    }
}
