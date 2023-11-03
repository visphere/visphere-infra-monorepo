/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.SyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultGuildProfileResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.guild.GuildCategory;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.guildlink.GuildLinkRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.guild.dto.*;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildServiceImpl implements GuildService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;

    private final GuildRepository guildRepository;
    private final GuildLinkRepository guildLinkRepository;

    @Override
    public CreateGuildResDto createGuild(AuthUserDetails user, CreateGuildReqDto reqDto) {
        final GuildEntity guild = GuildEntity.builder()
            .name(reqDto.getName())
            .category(reqDto.getCategory())
            .ownerId(user.getId())
            .build();

        final GuildEntity savedGuild = guildRepository.save(guild);

        final DefaultGuildProfileReqDto guildProfileReqDto = DefaultGuildProfileReqDto.builder()
            .guildName(savedGuild.getName())
            .guildId(savedGuild.getId())
            .build();

        final DefaultGuildProfileResDto resDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GENERATE_DEFAULT_GUILD_PROFILE, guildProfileReqDto,
                DefaultGuildProfileResDto.class);

        log.info("Successfully created new guild sphere: '{}'", savedGuild);
        return CreateGuildResDto.builder()
            .id(savedGuild.getId())
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_CREATED_RESPONSE_SUCCESS, Map.of(
                "sphereName", reqDto.getName()
            )))
            .profileUrl(resDto.imageFullPath())
            .build();
    }

    @Override
    @Transactional
    public UpdateGuildResDto updateGuildName(AuthUserDetails user, UpdateGuildNameReqDto reqDto, Long guildId) {
        final GuildEntity guild = getGuild(user, guildId);
        final String prevName = guild.getName();
        guild.setName(reqDto.getName());

        final DefaultGuildProfileReqDto guildProfileReqDto = DefaultGuildProfileReqDto.builder()
            .guildName(guild.getName())
            .guildId(guild.getId())
            .build();

        final DefaultGuildProfileResDto resDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.UPDATE_DEFAULT_GUILD_PROFILE, guildProfileReqDto,
                DefaultGuildProfileResDto.class);

        log.info("Successfully updated guild name from: '{}' to: '{}'", prevName, reqDto.getName());
        return UpdateGuildResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_UPDATE_NAME_RESPONSE_SUCCESS))
            .profileUrl(resDto.imageFullPath())
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuildCategory(AuthUserDetails user, UpdateGuildCategoryReqDto reqDto, Long guildId) {
        final GuildEntity guild = getGuild(user, guildId);

        final GuildCategory prevCategory = guild.getCategory();
        guild.setCategory(reqDto.getCategory());

        log.info("Successfully update guild category from: '{}' to: '{}'", prevCategory, reqDto.getCategory());
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_UPDATE_CATEGORY_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuildVisibility(AuthUserDetails user, UpdateGuildVisibilityReqDto reqDto, Long guildId) {
        final GuildEntity guild = getGuild(user, guildId);
        LocaleSet outputMessage = LocaleSet.SPHERE_GUILD_UPDATE_VISIBILITY_RESPONSE_SUCCESS;

        final boolean prevIsPrivate = guild.getPrivate();
        guild.setPrivate(reqDto.isPrivate());

        if (reqDto.isUnactiveAllPreviousLinks()) {
            guildLinkRepository.removeAllByGuild_Id(guildId);
            outputMessage = LocaleSet.SPHERE_GUILD_UPDATE_VISIBILITY_WITH_REMOVE_LINKS_RESPONSE_SUCCESS;
            log.info("Successfully removed all connected join sphere links from guild: '{}'", guild);
        }

        log.info("Successfully update guild private mode from: '{}' to: '{}'", prevIsPrivate, reqDto.isPrivate());
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(outputMessage))
            .build();
    }

    private GuildEntity getGuild(AuthUserDetails user, Long guildId) {
        return guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));
    }
}
