/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.network.profilecolor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.StringParser;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsResDto;
import pl.visphere.lib.kafka.payload.user.UserDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.*;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.multimedia.cache.CacheName;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileEntity;
import pl.visphere.multimedia.domain.accountprofile.AccountProfileRepository;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileEntity;
import pl.visphere.multimedia.domain.guildprofile.GuildProfileRepository;
import pl.visphere.multimedia.dto.MessageWithResourcePathResDto;
import pl.visphere.multimedia.exception.AccountProfileException;
import pl.visphere.multimedia.exception.GuildProfileException;
import pl.visphere.multimedia.i18n.LocaleSet;
import pl.visphere.multimedia.network.profilecolor.dto.UpdateProfileColorReqDto;
import pl.visphere.multimedia.processing.ImageProperties;
import pl.visphere.multimedia.processing.drawer.IdenticonDrawer;
import pl.visphere.multimedia.processing.drawer.ImageDrawer;
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
    private final ImageDrawer imageDrawer;
    private final CacheService cacheService;

    private final AccountProfileRepository accountProfileRepository;
    private final GuildProfileRepository guildProfileRepository;

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

        final char[] initials = {
            userDetailsResDto.getFirstName().charAt(0),
            userDetailsResDto.getLastName().charAt(0)
        };

        final byte[] updatedImage = switch (accountProfile.getImageType()) {
            case DEFAULT -> initialsDrawer.drawImage(initials, reqDto.getColor());
            case IDENTICON -> identiconDrawer.drawImage(user.getUsername(), reqDto.getColor());
            case CUSTOM -> new byte[0];
        };
        final FilePayload filePayload = new FilePayload(updatedImage, imageDrawer.getFileExtension());

        String fullPath = s3Client.createFullResourcePath(S3Bucket.USERS, user.getId(),
            filePayload, accountProfile.getProfileImageUuid());

        if (updatedImage.length > 0) {
            s3Client.clearObjects(S3Bucket.USERS, user.getId(), S3ResourcePrefix.PROFILE);
            final ObjectData res = s3Client.putObject(S3Bucket.USERS, user.getId(), filePayload);
            accountProfile.setProfileImageUuid(res.uuid());
            fullPath = res.fullPath();
        }
        accountProfile.setProfileColor(reqDto.getColor());
        cacheService.deleteCache(CacheName.ACCOUNT_PROFILE_ENTITY_USER_ID, user.getId());

        log.info("Successfully updated profile color from: '{}' to: '{}'.", prevColor, reqDto.getColor());
        return MessageWithResourcePathResDto.builder()
            .resourcePath(fullPath)
            .message(i18nService.getMessage(LocaleSet.USER_PROFILE_COLOR_UPDATE_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public MessageWithResourcePathResDto updateGuildProfileColor(
        long guildId, UpdateProfileColorReqDto reqDto, AuthUserDetails user
    ) {
        final GuildProfileEntity guildProfile = guildProfileRepository
            .findByGuildId(guildId)
            .orElseThrow(() -> new GuildProfileException.GuildProfileNotFoundException(guildId));

        final String prevColor = guildProfile.getProfileColor();
        final GuildDetailsReqDto guildDetailsReqDto = GuildDetailsReqDto.builder()
            .guildId(guildId)
            .loggedUserId(user.getId())
            .isModifiable(true)
            .build();

        final GuildDetailsResDto detailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_DETAILS, guildDetailsReqDto, GuildDetailsResDto.class);

        final byte[] updatedImage = switch (guildProfile.getImageType()) {
            case DEFAULT -> initialsDrawer
                .drawImage(StringParser.parseGuildNameInitials(detailsResDto.name()), reqDto.getColor());
            case CUSTOM, IDENTICON -> new byte[0];
        };
        final FilePayload filePayload = new FilePayload(updatedImage, imageDrawer.getFileExtension());

        String fullPath = s3Client.createFullResourcePath(S3Bucket.SPHERES, guildId,
            filePayload, guildProfile.getProfileImageUuid());

        if (updatedImage.length > 0) {
            s3Client.clearObjects(S3Bucket.SPHERES, guildId, S3ResourcePrefix.PROFILE);
            final ObjectData res = s3Client.putObject(S3Bucket.SPHERES, guildId, filePayload);
            guildProfile.setProfileImageUuid(res.uuid());
            fullPath = res.fullPath();
        }
        guildProfile.setProfileColor(reqDto.getColor());
        cacheService.deleteCache(CacheName.GUILD_PROFILE_ENTITY_GUILD_ID, guildId);

        log.info("Successfully updated guild profile color from: '{}' to: '{}'.", prevColor, reqDto.getColor());
        return MessageWithResourcePathResDto.builder()
            .resourcePath(fullPath)
            .message(i18nService.getMessage(LocaleSet.GUILD_PROFILE_COLOR_UPDATE_RESPONSE_SUCCESS))
            .build();
    }
}
