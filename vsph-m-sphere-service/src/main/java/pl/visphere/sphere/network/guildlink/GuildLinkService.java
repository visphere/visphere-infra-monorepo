/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.guildlink.dto.CreateGuildLinkReqDto;
import pl.visphere.sphere.network.guildlink.dto.GuildLinkResDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkActiveReqDto;
import pl.visphere.sphere.network.guildlink.dto.UpdateGuildLinkExpirationReqDto;

import java.util.List;

interface GuildLinkService {
    List<GuildLinkResDto> getAllLinksFromGuild(AuthUserDetails user, Long guildId);
    BaseMessageResDto createGuildLink(AuthUserDetails user, CreateGuildLinkReqDto reqDto, Long guildId);
    BaseMessageResDto updateExpiration(AuthUserDetails user, UpdateGuildLinkExpirationReqDto reqDto, Long linkId);
    BaseMessageResDto updateActive(AuthUserDetails user, UpdateGuildLinkActiveReqDto reqDto, Long linkId);
    BaseMessageResDto deleteGuildLink(AuthUserDetails user, Long linkId);
}
