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
import pl.visphere.sphere.network.guildlink.dto.CreateGuildLinkReqDto;
import pl.visphere.sphere.network.guildlink.dto.GuildLinkResDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkActiveReqDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkExpirationReqDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class GuildLinkServiceImpl implements GuildLinkService {
    private final I18nService i18nService;
    private final GuildLinkProperties guildLinkProperties;

    private final GuildLinkRepository guildLinkRepository;
    private final GuildRepository guildRepository;

    @Override
    public List<GuildLinkResDto> getAllLinksFromGuild(AuthUserDetails user, Long guildId) {
        return guildLinkRepository
            .findByGuild_Id(guildId)
            .stream().map(link -> new GuildLinkResDto(link, guildLinkProperties.getUrlPrefix()))
            .collect(Collectors.toList());
    }

    @Override
    public BaseMessageResDto createGuildLink(AuthUserDetails user, CreateGuildLinkReqDto reqDto, Long guildId) {
        final ZonedDateTime expiredAt = reqDto.getExpiredAt();
        if (expiredAt.isBefore(ZonedDateTime.now())) {
            throw new SphereGuildLinkException.SphereGuildLinkIncorrectTimeException(expiredAt);
        }
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(10);
        } while (guildLinkRepository.existsByToken(token));

        final GuildLinkEntity guildLink = GuildLinkEntity.builder()
            .token(token)
            .expiredAt(expiredAt)
            .isActive(true)
            .guild(guild)
            .build();

        final GuildLinkEntity savedGuildLink = guildLinkRepository.save(guildLink);

        log.info("Successfully created new guild link: '{}'.", savedGuildLink);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_CREATED_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateExpiration(AuthUserDetails user, UpdateGuildLinkExpirationReqDto reqDto, Long linkId) {
        final ZonedDateTime expiredAt = reqDto.getNewExpirationTime();
        if (expiredAt.isBefore(ZonedDateTime.now())) {
            throw new SphereGuildLinkException.SphereGuildLinkIncorrectTimeException(expiredAt);
        }
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerId(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        final ZonedDateTime prevExpiredTime = guildLink.getExpiredAt();
        guildLink.setExpiredAt(reqDto.getNewExpirationTime());

        log.info("Successfully updated link expiration time from: '{}' to: '{}'.", expiredAt, prevExpiredTime);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_UPDATED_EXPIRATION_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto updateActive(AuthUserDetails user, UpdateGuildLinkActiveReqDto reqDto, Long linkId) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerId(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        final boolean prevActive = guildLink.getActive();
        guildLink.setActive(reqDto.isActive());

        log.info("Successfully updated link active state from: '{}' to: '{}'.", prevActive, reqDto.isActive());
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_UPDATED_ACTIVE_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto deleteGuildLink(AuthUserDetails user, Long linkId) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByIdAndGuild_OwnerId(linkId, user.getId())
            .orElseThrow(() -> new SphereGuildLinkException.SphereGuildLinkNotFoundException(linkId));

        guildLinkRepository.delete(guildLink);

        log.info("Successfully deleted link: '{}'.", guildLink);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_DELETED_RESPONSE_SUCCESS))
            .build();
    }
}
