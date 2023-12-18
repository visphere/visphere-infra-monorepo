/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.service.sphereguild;

import pl.visphere.lib.kafka.payload.sphere.GuildDetailsReqDto;
import pl.visphere.lib.kafka.payload.sphere.GuildDetailsResDto;

public interface SphereGuildService {
    GuildDetailsResDto getGuildDetails(GuildDetailsReqDto reqDto);
}