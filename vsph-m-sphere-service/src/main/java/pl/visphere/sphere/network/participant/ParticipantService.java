/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.participant.dto.BannerMemberDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipantDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipantsResDto;

import java.util.List;

interface ParticipantService {
    GuildParticipantsResDto getAllGuildParticipants(long guildId, AuthUserDetails user);
    GuildParticipantDetailsResDto getGuildParticipantDetails(long guildId, long userId, AuthUserDetails user);
    List<BannerMemberDetailsResDto> getAllBannedParticipants(long guildId, AuthUserDetails user);
    BaseMessageResDto leaveGuild(long guildId, boolean deleteAllMessages, AuthUserDetails user);
    BaseMessageResDto kickFromGuild(long guildId, long userId, boolean deleteAllMessages, AuthUserDetails user);
    BaseMessageResDto unbanFromGuild(long guildId, long userId, AuthUserDetails user);
    BaseMessageResDto banFromGuild(long guildId, long userId, boolean deleteAllMessages, AuthUserDetails user);
}
