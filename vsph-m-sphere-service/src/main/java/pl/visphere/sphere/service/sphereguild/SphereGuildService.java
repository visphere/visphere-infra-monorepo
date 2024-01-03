/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.service.sphereguild;

import pl.visphere.lib.kafka.payload.sphere.*;

public interface SphereGuildService {
    GuildDetailsResDto getGuildDetails(GuildDetailsReqDto reqDto);
    UserTextChannelsResDto getUserTextChannels(Long userId);
    boolean checkUserSphereGuilds(Long userId);
    boolean checkUserGuildAssignments(GuildAssignmentsReqDto reqDto);
    void checkTextChannelAssignments(TextChannelAssignmentsReqDto reqDto);
    void deleteUserFromGuilds(Long userId);
    GuildByTextChannelIdResDto getGuildBaseTextChannelId(Long textChannelId);
}
