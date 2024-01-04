/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.service.sphereguild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.lib.kafka.payload.sphere.*;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.textchannel.TextChannelEntity;
import pl.visphere.sphere.domain.textchannel.TextChannelRepository;
import pl.visphere.sphere.domain.userguild.UserGuildRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.TextChannelException;
import pl.visphere.sphere.exception.UserGuildException;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SphereGuildServiceImpl implements SphereGuildService {
    private final GuildRepository guildRepository;
    private final UserGuildRepository userGuildRepository;
    private final TextChannelRepository textChannelRepository;

    @Override
    public GuildDetailsResDto getGuildDetails(GuildDetailsReqDto reqDto) {
        final Long guildId = reqDto.guildId();
        final GuildEntity guild = guildRepository
            .findById(guildId)
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        if (reqDto.isModifiable() && !Objects.equals(guild.getOwnerId(), reqDto.loggedUserId())) {
            throw new SphereGuildException.SphereGuildNotFoundException(guildId);
        }
        final GuildDetailsResDto resDto = GuildDetailsResDto.builder()
            .name(guild.getName())
            .ownerId(guild.getOwnerId())
            .createdDate(guild.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
            .build();

        log.info("Successfully fetched guild with ID: '{}' and parse to details: '{}'.", guildId, resDto);
        return resDto;
    }

    @Override
    public UserTextChannelsResDto getUserTextChannels(Long userId) {
        final List<Long> guildIds = userGuildRepository
            .findAllByUserId(userId)
            .stream().map(userGuild -> userGuild.getGuild().getId())
            .toList();

        final List<Long> textChannelsIds = textChannelRepository
            .findAllByGuild_IdIn(guildIds)
            .stream().map(AbstractAuditableEntity::getId)
            .toList();

        log.info("Successfully find text channel IDs: '{}' for user: '{}'.", textChannelsIds, userId);
        return new UserTextChannelsResDto(textChannelsIds);
    }

    @Override
    public boolean checkUserSphereGuilds(Long userId) {
        final int userGuilds = guildRepository.countAllByOwnerId(userId);
        log.info("Successfully found user with ID: '{}' account with some Sphere guilds correlatios: '{}'.",
            userId, userGuilds);
        return userGuilds != 0;
    }

    @Override
    public boolean checkUserGuildAssignments(GuildAssignmentsReqDto reqDto) {
        final boolean userExistInGuild = userGuildRepository
            .existsByUserIdAndGuild_Id(reqDto.userId(), reqDto.guildId());
        if (!userExistInGuild && reqDto.throwingError()) {
            throw new UserGuildException.UserIsNotGuildParticipantException(reqDto.userId(), reqDto.guildId());
        }
        return userExistInGuild;
    }

    @Override
    public void checkTextChannelAssignments(TextChannelAssignmentsReqDto reqDto) {
        final TextChannelEntity textChannel = textChannelRepository
            .findById(reqDto.textChannelId())
            .orElseThrow(() -> new TextChannelException.TextChannelNotFoundException(reqDto.textChannelId()));

        final Long guildId = textChannel.getGuild().getId();
        if (!userGuildRepository.existsByUserIdAndGuild_Id(reqDto.userId(), guildId)) {
            throw new TextChannelException.TextChannelNotFoundException(reqDto.textChannelId());
        }
        log.info("Successfully found text channel: '{}' for user: '{}' and guild: '{}'.",
            reqDto.textChannelId(), reqDto.userId(), guildId);
    }

    @Override
    @Transactional
    public void deleteUserFromGuilds(Long userId) {
        userGuildRepository.deleteAllByUserId(userId);
        log.info("Successfully deleted user guild relations with user ID: '{}'.", userId);
    }

    @Override
    public GuildByTextChannelIdResDto getGuildBaseTextChannelId(Long textChannelId) {
        final TextChannelEntity textChannel = textChannelRepository
            .findById(textChannelId)
            .orElseThrow(() -> new TextChannelException.TextChannelNotFoundException(textChannelId));
        return new GuildByTextChannelIdResDto(textChannel.getGuild().getId());
    }
}
