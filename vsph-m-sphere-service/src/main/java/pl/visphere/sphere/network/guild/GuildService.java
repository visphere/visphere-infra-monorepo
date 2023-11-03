/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guild;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.guild.dto.*;

interface GuildService {
    CreateGuildResDto createGuild(AuthUserDetails user, CreateGuildReqDto reqDto);
    UpdateGuildResDto updateGuildName(AuthUserDetails user, UpdateGuildNameReqDto reqDto, Long guildId);
    BaseMessageResDto updateGuildCategory(AuthUserDetails user, UpdateGuildCategoryReqDto reqDto, Long guildId);
    BaseMessageResDto updateGuildVisibility(AuthUserDetails user, UpdateGuildVisibilityReqDto reqDto, Long guildId);
}
