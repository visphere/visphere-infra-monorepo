/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.textchannel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.textchannel.TextChannelEntity;
import pl.visphere.sphere.domain.textchannel.TextChannelRepository;
import pl.visphere.sphere.domain.userguild.UserGuildRepository;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.TextChannelException;
import pl.visphere.sphere.exception.UserGuildException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.textchannel.dto.CreateTextChannelReqDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelDetailsResDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelResDto;
import pl.visphere.sphere.network.textchannel.dto.UpdateTextChannelReqDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class TextChannelServiceImpl implements TextChannelService {
    private final I18nService i18nService;

    private final GuildRepository guildRepository;
    private final UserGuildRepository userGuildRepository;
    private final TextChannelRepository textChannelRepository;

    @Override
    public TextChannelDetailsResDto getTextChannelDetails(long textChannelId, AuthUserDetails user) {
        final TextChannelEntity textChannel = textChannelRepository
            .findById(textChannelId)
            .orElseThrow(() -> new TextChannelException.TextChannelNotFoundException(textChannelId));

        final Long guildId = textChannel.getGuild().getId();
        if (!userGuildRepository.existsByUserIdAndGuild_Id(user.getId(), guildId)) {
            throw new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId);
        }
        final TextChannelDetailsResDto resDto = TextChannelDetailsResDto.builder()
            .name(textChannel.getName())
            .build();

        log.info("Sucessfully parsed text channel with ID: '{}' to data: '{}'.", textChannelId, resDto);
        return resDto;
    }

    @Override
    public List<TextChannelResDto> getGuildTextChannels(long guildId, AuthUserDetails user) {
        if (!userGuildRepository.existsByUserIdAndGuild_Id(user.getId(), guildId)) {
            throw new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId);
        }
        return textChannelRepository
            .findAllByGuild_Id(guildId)
            .stream()
            .map(textChannel -> new TextChannelResDto(textChannel.getId(), textChannel.getName()))
            .toList();
    }

    @Override
    @Transactional
    public BaseMessageResDto createTextChannel(long guildId, CreateTextChannelReqDto reqDto, AuthUserDetails user) {
        if (textChannelRepository.existsByGuild_IdAndName(guildId, reqDto.getName())) {
            throw new TextChannelException.TextChannelDuplicateNameException(guildId);
        }
        final GuildEntity guild = guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final TextChannelEntity textChannelEntity = TextChannelEntity.builder()
            .name(reqDto.getName())
            .build();
        guild.persistTextChannel(textChannelEntity);

        log.info("Successfully created new text channel for guild with ID: '{}', {}", guildId, textChannelEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_TEXT_CHANNEL_CREATED_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto updateTextChannel(long textChannelId, UpdateTextChannelReqDto reqDto, AuthUserDetails user) {
        final TextChannelEntity textChannel = findTextChannel(textChannelId, user);
        final boolean isDuplicatedName = textChannelRepository
            .existsByGuild_IdAndNameAndIdNot(textChannel.getGuild().getId(), reqDto.getName(), textChannel.getId());

        if (isDuplicatedName) {
            throw new TextChannelException.TextChannelDuplicateNameException(textChannel.getGuild().getId());
        }
        textChannel.setName(reqDto.getName());

        log.info("Successfully updated text channel with ID: '{}' and data: '{}'.", textChannel, reqDto);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_TEXT_CHANNEL_UPDATED_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto deleteTextChannel(long textChannelId, AuthUserDetails user) {
        final TextChannelEntity textChannel = findTextChannel(textChannelId, user);

        textChannel.setGuild(null);
        textChannelRepository.deleteById(textChannelId);

        // TODO: delete all messages from guild (chat microservice)

        log.info("Successfully deleted text channel with ID: '{}' from guild.", textChannelId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_TEXT_CHANNEL_DELETED_RESPONSE_SUCCESS))
            .build();
    }

    private TextChannelEntity findTextChannel(long textChannelId, AuthUserDetails user) {
        return textChannelRepository
            .findByIdAndGuild_OwnerId(textChannelId, user.getId())
            .orElseThrow(() -> new TextChannelException.TextChannelNotFoundException(textChannelId));
    }
}
