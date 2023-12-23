/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.joinguild;

import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.joinguild.dto.JoinGuildResDto;
import pl.visphere.sphere.network.joinguild.dto.JoiningGuildDetailsResDto;

interface JoinGuildService {
    JoiningGuildDetailsResDto getPrivateGuildDetails(String code, AuthUserDetails user);
    JoiningGuildDetailsResDto getPublicGuildDetails(long guildId, AuthUserDetails user);
    JoinGuildResDto joinToPrivateGuildViaCode(String code, AuthUserDetails user);
    JoinGuildResDto joinToPublicGuild(long guildId, AuthUserDetails user);
}
