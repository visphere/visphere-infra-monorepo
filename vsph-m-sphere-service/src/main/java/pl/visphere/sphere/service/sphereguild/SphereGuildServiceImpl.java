/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.service.sphereguild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsResDto;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.exception.SphereGuildException;

import java.time.ZoneId;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SphereGuildServiceImpl implements SphereGuildService {
    private final GuildRepository guildRepository;

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
    public boolean checkUserSphereGuilds(Long userId) {
        final int userGuilds = guildRepository.countAllByOwnerId(userId);
        log.info("Successfully found user with ID: '{}' account with some Sphere guilds correlatios: '{}'.",
            userId, userGuilds);
        return userGuilds != 0;
    }
}
