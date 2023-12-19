/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.textchannel;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.network.textchannel.dto.CreateTextChannelReqDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelDetailsResDto;
import pl.visphere.sphere.network.textchannel.dto.TextChannelResDto;
import pl.visphere.sphere.network.textchannel.dto.UpdateTextChannelReqDto;

import java.util.List;

interface TextChannelService {
    TextChannelDetailsResDto getTextChannelDetails(long textChannelId, AuthUserDetails user);
    List<TextChannelResDto> getGuildTextChannels(long guildId, AuthUserDetails user);
    BaseMessageResDto createTextChannel(long guildId, CreateTextChannelReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateTextChannel(long textChannelId, UpdateTextChannelReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto deleteTextChannel(long textChannelId, AuthUserDetails user);
}
