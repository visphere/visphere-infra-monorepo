/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.guildlink.dto.*;

import java.util.List;

interface GuildLinkService {
    AllGuildJoinLinksResDto getAllLinksFromGuild(long guildId, AuthUserDetails user);
    List<ExpireTimestamp> getAllExpiredTimestamps();
    GuildLinkDetailsResDto getGuildLinkDetails(long linkId, AuthUserDetails user);
    BaseMessageResDto createGuildLink(long guildId, CreateGuildLinkReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateGuildLink(long linkId, UpdateGuildLinkReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateGuildLinkActiveState(long linkId, boolean active, AuthUserDetails user);
    BaseMessageResDto deleteGuildLink(long linkId, AuthUserDetails user);
}
