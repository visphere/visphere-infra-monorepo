/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.service.sphereguild;

import pl.visphere.lib.kafka.payload.sphere.GuildAssignmentsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsResDto;
import pl.visphere.lib.kafka.payload.sphere.TextChannelAssignmentsReqDto;

public interface SphereGuildService {
    GuildDetailsResDto getGuildDetails(GuildDetailsReqDto reqDto);
    boolean checkUserSphereGuilds(Long userId);
    boolean checkUserGuildAssignments(GuildAssignmentsReqDto reqDto);
    void checkTextChannelAssignments(TextChannelAssignmentsReqDto reqDto);
    void deleteUserFromGuilds(Long userId);
}
