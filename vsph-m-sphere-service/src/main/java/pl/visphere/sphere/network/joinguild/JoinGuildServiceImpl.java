/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.banneduser.BannedUserRepository;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.guildlink.GuildLinkEntity;
import pl.visphere.sphere.domain.guildlink.GuildLinkRepository;
import pl.visphere.sphere.domain.userguild.UserGuildEntity;
import pl.visphere.sphere.domain.userguild.UserGuildRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.SphereGuildJoinException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.joinguild.dto.JoinGuildResDto;
import pl.visphere.sphere.network.joinguild.dto.JoiningGuildDetailsResDto;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
class JoinGuildServiceImpl implements JoinGuildService {
    private final I18nService i18nService;
    private final JoinGuildMapper joinGuildMapper;
    private final SyncQueueHandler syncQueueHandler;

    private final GuildRepository guildRepository;
    private final UserGuildRepository userGuildRepository;
    private final GuildLinkRepository guildLinkRepository;
    private final BannedUserRepository bannedUserRepository;

    @Override
    @Transactional
    public JoiningGuildDetailsResDto getPrivateGuildDetails(String code, AuthUserDetails user) {
        final GuildLinkEntity guildLink = fetchGuildLinkAndValidate(code, user);
        final GuildEntity guild = guildLink.getGuild();

        final JoiningGuildDetailsResDto resDto = parseToGuildDetails(guild, user);
        guildLink.setUsagesCount(guildLink.getUsagesCount() + 1);

        log.info("Successfully found private guild with ID: '{}' and parsed response: '{}'.", guild.getId(), resDto);
        return resDto;
    }

    @Override
    public JoiningGuildDetailsResDto getPublicGuildDetails(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findByIdAndIsPrivateIsFalse(guildId)
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final JoiningGuildDetailsResDto resDto = parseToGuildDetails(guild, user);

        log.info("Successfully found public guild with ID: '{}' and parsed response: '{}'.", guild.getId(), resDto);
        return resDto;
    }

    @Override
    @Transactional
    public JoinGuildResDto joinToPrivateGuildViaCode(String code, AuthUserDetails user) {
        final GuildLinkEntity guildLink = fetchGuildLinkAndValidate(code, user);
        final GuildEntity guild = guildLink.getGuild();

        final JoinGuildResDto resDto = checkUserAndAddToGuild(guild, user);
        guildLink.setUsagesCount(guildLink.getUsagesCount() + 1);

        log.info("Successfully add user: '{}' to private guild: '{}'.", user, guild);
        return resDto;
    }

    @Override
    public JoinGuildResDto joinToPublicGuild(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findById(guildId)
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final JoinGuildResDto resDto = checkUserAndAddToGuild(guild, user);

        log.info("Successfully add user: '{}' to public guild: '{}'.", user, guild);
        return resDto;
    }

    private GuildLinkEntity fetchGuildLinkAndValidate(String code, AuthUserDetails user) {
        final GuildLinkEntity guildLink = guildLinkRepository
            .findByTokenAndGuild_IsPrivateIsTrueAndIsActiveIsTrue(code)
            .orElseThrow(SphereGuildException.SphereGuildByCodeNotFoundException::new);

        final GuildEntity guild = guildLink.getGuild();
        if (guildLink.getExpiredAt() != null && guildLink.getExpiredAt().isBefore(ZonedDateTime.now())) {
            throw new SphereGuildJoinException.JoinLinkExpiredException(guild.getId(), user.getId());
        }
        return guildLink;
    }

    private JoiningGuildDetailsResDto parseToGuildDetails(GuildEntity guild, AuthUserDetails user) {
        validateUserWithGuild(guild, user);
        final ProfileImageDetailsResDto profileImageDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_PROFILE_IMAGE_DETAILS, guild.getId(),
                ProfileImageDetailsResDto.class);

        final int participants = userGuildRepository.countAllByGuild_Id(guild.getId());
        return joinGuildMapper
            .mapToJoiningGuildDetails(guild, participants, profileImageDetailsResDto);
    }

    private JoinGuildResDto checkUserAndAddToGuild(GuildEntity guild, AuthUserDetails user) {
        validateUserWithGuild(guild, user);
        final UserGuildEntity userGuild = UserGuildEntity.builder()
            .userId(user.getId())
            .build();

        guild.persistUserGuild(userGuild);

        return JoinGuildResDto.builder()
            .guildId(guild.getId())
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LINK_JOIN_RESPONSE_SUCCESS))
            .build();
    }

    private void validateUserWithGuild(GuildEntity guild, AuthUserDetails user) {
        if (userGuildRepository.existsByUserIdAndGuild_Id(user.getId(), guild.getId())) {
            throw new SphereGuildJoinException.AlreadyIsOnSphereException(guild.getId(), user.getId());
        }
        if (bannedUserRepository.existsByGuild_IdAndUserId(guild.getId(), user.getId())) {
            throw new SphereGuildException.SphereGuildByCodeNotFoundException();
        }
    }
}
