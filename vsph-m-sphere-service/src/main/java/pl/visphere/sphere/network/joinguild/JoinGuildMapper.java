/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.network.joinguild.dto.JoiningGuildDetailsResDto;

@Component
@RequiredArgsConstructor
class JoinGuildMapper {
    private final ModelMapper modelMapper;

    JoiningGuildDetailsResDto mapToJoiningGuildDetails(
        GuildEntity guild, int participants, ProfileImageDetailsResDto profileImageDetailsResDto
    ) {
        final JoiningGuildDetailsResDto resDto = modelMapper.map(guild, JoiningGuildDetailsResDto.class);
        resDto.setParticipants(participants);
        resDto.setProfileColor(profileImageDetailsResDto.getProfileColor());
        resDto.setProfileImageUrl(profileImageDetailsResDto.getProfileImagePath());
        return resDto;
    }
}
