/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.guildlink.GuildLinkEntity;
import pl.visphere.sphere.domain.guildlink.GuildLinkRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.SphereGuildLinkException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.guildlink.dto.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class GuildLinkServiceImpl implements GuildLinkService {
    private final I18nService i18nService;
    private final GuildLinkProperties guildLinkProperties;

    private final GuildLinkRepository guildLinkRepository;
    private final GuildRepository guildRepository;

    @Override
    public AllGuildJoinLinksResDto getAllLinksFromGuild(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final List<GuildLinkEntity> guildLinks = guildLinkRepository
            .findAllByGuild_IdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(guildId, user.getId());

        final List<GuildLinkDetails> resDtos = new ArrayList<>(guildLinks.size());
        final List<Long> deletingObjects = new ArrayList<>(guildLinks.size());

        for (final GuildLinkEntity guildLink : guildLinks) {
            final ZonedDateTime expiredAt = guildLink.getExpiredAt();
            if (expiredAt != null && expiredAt.isBefore(ZonedDateTime.now())) {
                deletingObjects.add(guildLink.getId());
            } else {
                resDtos.add(new GuildLinkDetails(guildLink, guildLinkProperties.getUrlPrefix()));
            }
        }
        guildLinkRepository.deleteAllById(deletingObjects);

        log.info("Successfully found: '{}' links and remove: '{}' links.", guildLinks.size(), deletingObjects.size());
        return AllGuildJoinLinksResDto.builder()
            .isPrivate(guild.getPrivate())
            .joinLinks(resDtos)
            .build();
    }

    @Override
    public List<ExpireTimestamp> getAllExpiredTimestamps() {
        return Arrays.stream(ExpiredAfter.values())
            .map(timestamp -> new ExpireTimestamp(timestamp.name(), timestamp.name().toLowerCase()))
            .toList();
    }

    @Override
    public GuildLinkDetailsResDto getGuildLinkDetails(long linkId, AuthUserDetails user) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        final GuildLinkDetailsResDto resDto = GuildLinkDetailsResDto.builder()
            .id(guildLink.getId())
            .name(guildLink.getName())
            .build();

        log.info("Successfully found guild link with ID: '{}' and parse to details: '{}'.", linkId, resDto);
        return resDto;
    }

    @Override
    @Transactional
    public BaseMessageResDto createGuildLink(long guildId, CreateGuildLinkReqDto reqDto, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerIdAndIsPrivateIsTrue(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(10);
        } while (guildLinkRepository.existsByToken(token));

        final ExpiredAfter after = reqDto.getExpiredAfter();
        final ZonedDateTime expirationDate = !after.equals(ExpiredAfter.NEVER)
            ? ZonedDateTime.now().plus(after.getTime(), after.getUnit())
            : null;

        final GuildLinkEntity guildLink = GuildLinkEntity.builder()
            .name(reqDto.getName())
            .token(token)
            .expiredAt(expirationDate)
            .isActive(true)
            .build();
        guild.persistGuildLink(guildLink);

        log.info("Successfully created new guild link: '{}'.", guildLink);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_CREATED_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuildLink(long linkId, UpdateGuildLinkReqDto reqDto, AuthUserDetails user) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        guildLink.setName(reqDto.getName());

        log.info("Successfully updated guild link to: '{}'.", guildLink);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_UPDATED_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateGuildLinkActiveState(long linkId, boolean active, AuthUserDetails user) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        guildLink.setActive(active);

        final LocaleSet message = active
            ? LocaleSet.SPHERE_GUILD_LINK_ACTIVE_UPDATED_RESPONSE_SUCCESS
            : LocaleSet.SPHERE_GUILD_LINK_INACTIVE_UPDATED_RESPONSE_SUCCESS;

        log.info("Successfully update join link: '{}' active state to: '{}'.", guildLink,
            active ? "ACTIVE" : "INACTIVE");
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(message))
            .build();
    }

    @Override
    public BaseMessageResDto deleteGuildLink(long linkId, AuthUserDetails user) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        guildLinkRepository.delete(guildLink);

        log.info("Successfully deleted link: '{}'.", guildLink);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_DELETED_RESPONSE_SUCCESS))
            .build();
    }
}
