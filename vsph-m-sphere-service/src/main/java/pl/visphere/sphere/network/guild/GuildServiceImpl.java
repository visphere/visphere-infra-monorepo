/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.chat.DeleteTextChannelMessagesReqDto;
import pl.visphere.lib.kafka.payload.multimedia.*;
import pl.visphere.lib.kafka.payload.user.CredentialsConfirmationReqDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.S3Bucket;
import pl.visphere.lib.s3.S3Client;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.guild.GuildCategory;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.guildlink.GuildLinkRepository;
import pl.visphere.sphere.domain.textchannel.TextChannelEntity;
import pl.visphere.sphere.domain.textchannel.TextChannelRepository;
import pl.visphere.sphere.domain.userguild.UserGuildEntity;
import pl.visphere.sphere.domain.userguild.UserGuildRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.UserGuildException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.guild.dto.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildServiceImpl implements GuildService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;
    private final S3Client s3Client;

    private final GuildRepository guildRepository;
    private final GuildLinkRepository guildLinkRepository;
    private final UserGuildRepository userGuildRepository;
    private final TextChannelRepository textChannelRepository;

    @Override
    public GuildDetailsResDto getGuildDetails(long guildId, AuthUserDetails user) {
        final UserGuildEntity userGuild = userGuildRepository
            .findByUserIdAndGuild_Id(user.getId(), guildId)
            .orElseThrow(() -> new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId));

        final GuildEntity guild = userGuild.getGuild();

        final ProfileImageDetailsResDto profileImageDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_PROFILE_IMAGE_DETAILS, guildId,
                ProfileImageDetailsResDto.class);

        final GuildDetailsResDto resDto = GuildDetailsResDto.builder()
            .id(guild.getId())
            .name(guild.getName())
            .category(guild.getCategory().name())
            .profileColor(profileImageDetailsResDto.getProfileColor())
            .profileImageUrl(profileImageDetailsResDto.getProfileImagePath())
            .isPrivate(guild.getPrivate())
            .ownerId(guild.getOwnerId())
            .isLoggedUserIsOwner(guild.getOwnerId().equals(user.getId()))
            .build();

        log.info("Successfully parse details: '{}' with guild ID: '{}'", resDto, guildId);
        return resDto;
    }

    @Override
    public GuildOwnerDetailsResDto getGuildOwnerDetails(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final GuildOwnerDetailsResDto resDto = GuildOwnerDetailsResDto.builder()
            .id(guild.getId())
            .name(guild.getName())
            .build();

        log.info("Successfully found sphere guild with ID: '{}' and parse owner details: '{}'.", guildId, resDto);
        return resDto;
    }

    @Override
    public GuildOwnerOverviewResDto getGuildOwnerOverview(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final GuildOwnerOverviewResDto resDto = GuildOwnerOverviewResDto.builder()
            .id(guild.getId())
            .name(guild.getName())
            .category(guild.getCategory().name())
            .isPrivate(guild.getPrivate())
            .categories(getGuildCategories())
            .build();

        log.info("Successfully found sphere guild with ID: '{}' and parse owner overview: '{}'.", guildId, resDto);
        return resDto;
    }

    @Override
    public List<UserGuildResDto> getAllGuildsForUser(AuthUserDetails user) {
        final List<UserGuildEntity> guildIds = userGuildRepository.findAllByUserId(user.getId());
        final List<GuildEntity> guilds = guildIds.stream().map(UserGuildEntity::getGuild).toList();

        final GuildImageByIdsResDto guildImagesRes = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_IMAGES_BY_GUILD_IDS,
                new GuildImageByIdsReqDto(guilds.stream().map(AbstractAuditableEntity::getId).toList()),
                GuildImageByIdsResDto.class);

        final List<UserGuildResDto> resDtos = new ArrayList<>(guilds.size());
        for (final GuildEntity guild : guilds) {
            final GuildImageData image = guildImagesRes.guildImages().stream()
                .filter(guildImage -> Objects.equals(guildImage.guildId(), guild.getId()))
                .findFirst()
                .orElse(new GuildImageData(guild.getId(), StringUtils.EMPTY));

            final UserGuildResDto resDto = UserGuildResDto.builder()
                .id(guild.getId())
                .name(guild.getName())
                .profileUrl(image.imageUrl())
                .build();
            resDtos.add(resDto);
        }
        log.info("Succesfully found: '{}' guilds and parsed to user: '{}'", resDtos.size(), resDtos);
        return resDtos;
    }

    @Override
    public List<GuildCategoryResDto> getGuildCategories() {
        return Arrays.stream(GuildCategory.values())
            .map(category -> new GuildCategoryResDto(CaseUtils.toCamelCase(category.name(), false, '_'), category.name()))
            .toList();
    }

    @Override
    @Transactional
    public CreateGuildResDto createGuild(CreateGuildReqDto reqDto, AuthUserDetails user) {
        final UserGuildEntity userGuild = UserGuildEntity.builder()
            .userId(user.getId())
            .build();

        final GuildEntity guild = GuildEntity.builder()
            .name(reqDto.getName())
            .category(reqDto.getCategory())
            .isPrivate(reqDto.getIsPrivate())
            .ownerId(user.getId())
            .userGuilds(new HashSet<>())
            .build();
        guild.persistUserGuild(userGuild);

        final GuildEntity savedGuild = guildRepository.save(guild);

        final DefaultGuildProfileReqDto guildProfileReqDto = DefaultGuildProfileReqDto.builder()
            .guildName(savedGuild.getName())
            .guildId(savedGuild.getId())
            .build();

        final DefaultGuildProfileResDto resDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GENERATE_DEFAULT_GUILD_PROFILE, guildProfileReqDto,
                DefaultGuildProfileResDto.class);

        final String message = i18nService.getMessage(LocaleSet.SPHERE_GUILD_CREATED_RESPONSE_SUCCESS, Map.of(
            "sphereName", reqDto.getName()
        ));

        log.info("Successfully created new guild sphere: '{}'.", savedGuild);
        return CreateGuildResDto.builder()
            .id(savedGuild.getId())
            .message(message)
            .profileUrl(resDto.imageFullPath())
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuild(long guildId, UpdateGuildReqDto reqDto, AuthUserDetails user) {
        final GuildEntity guild = getGuild(user, guildId);

        final DefaultGuildProfileReqDto guildProfileReqDto = DefaultGuildProfileReqDto.builder()
            .guildId(guild.getId())
            .guildName(reqDto.getName())
            .build();

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.UPDATE_DEFAULT_GUILD_PROFILE, guildProfileReqDto);

        guild.setName(reqDto.getName());
        guild.setCategory(reqDto.getCategory());

        log.info("Successfully updated guild details to: '{}'.", guild);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_UPDATE_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuildVisibility(long guildId, UpdateGuildVisibilityReqDto reqDto, AuthUserDetails user) {
        final GuildEntity guild = getGuild(user, guildId);
        LocaleSet outputMessage = LocaleSet.SPHERE_GUILD_UPDATE_VISIBILITY_RESPONSE_SUCCESS;

        final boolean prevIsPrivate = guild.getPrivate();
        guild.setPrivate(reqDto.getIsPrivate());

        if (reqDto.getUnactiveAllPreviousLinks()) {
            guildLinkRepository.deleteAllByGuild_Id(guildId);
            outputMessage = LocaleSet.SPHERE_GUILD_UPDATE_VISIBILITY_WITH_REMOVE_LINKS_RESPONSE_SUCCESS;
            log.info("Successfully removed all connected join sphere links from guild: '{}'.", guild);
        }

        log.info("Successfully update guild private mode from: '{}' to: '{}'.", prevIsPrivate, reqDto.getIsPrivate());
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(outputMessage))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto deleteGuild(long guildId, PasswordReqDto reqDto, AuthUserDetails user) {
        final CredentialsConfirmationReqDto confirmationReqDto = CredentialsConfirmationReqDto.builder()
            .userId(user.getId())
            .password(reqDto.getPassword())
            .mfaCode(reqDto.getMfaCode())
            .build();
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.CHECK_USER_CREDENTIALS, confirmationReqDto);

        if (!guildRepository.existsByIdAndOwnerId(guildId, user.getId())) {
            throw new SphereGuildException.SphereGuildNotFoundException(guildId);
        }
        final List<Long> textChannelIds = textChannelRepository
            .findAllByGuild_Id(guildId).stream()
            .map(TextChannelEntity::getId)
            .toList();

        guildRepository.deleteById(guildId);

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_TEXT_CHANNEL_MESSAGES,
            new DeleteTextChannelMessagesReqDto(textChannelIds));

        s3Client.clearObjects(S3Bucket.ATTACHMENTS, guildId);

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_GUILD_IMAGE_DATA, guildId);

        log.info("Successfully delete guild with ID: '{}'.", guildId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_DELETED_RESPONSE_SUCCESS))
            .build();
    }

    private GuildEntity getGuild(AuthUserDetails user, Long guildId) {
        return guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));
    }
}
