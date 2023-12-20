/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.guild.dto.*;

import java.util.List;

interface GuildService {
    GuildDetailsResDto getGuildDetails(long guildId, AuthUserDetails user);
    GuildOwnerDetailsResDto getGuildOwnerDetails(long guildId, AuthUserDetails user);
    GuildOwnerOverviewResDto getGuildOwnerOverview(long guildId, AuthUserDetails user);
    List<UserGuildResDto> getAllGuildsForUser(AuthUserDetails user);
    List<GuildCategoryResDto> getGuildCategories();
    CreateGuildResDto createGuild(CreateGuildReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateGuild(long guildId, UpdateGuildReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateGuildVisibility(long guildId, UpdateGuildVisibilityReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto deleteGuild(long guildId, PasswordReqDto reqDto, AuthUserDetails user);
}
