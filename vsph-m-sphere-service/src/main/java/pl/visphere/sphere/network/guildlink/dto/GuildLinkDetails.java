/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink.dto;

import lombok.Getter;
import pl.visphere.sphere.domain.guildlink.GuildLinkEntity;

import java.time.ZonedDateTime;

@Getter
public class GuildLinkDetails {
    private final long id;
    private final String name;
    private final String joinLinkUrl;
    private final String token;
    private final ZonedDateTime expiredAt;
    private final boolean isActive;
    private final long usagesCount;

    public GuildLinkDetails(GuildLinkEntity entity, String urlPrefix) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.joinLinkUrl = urlPrefix + "/" + entity.getToken();
        this.token = entity.getToken();
        this.expiredAt = entity.getExpiredAt();
        this.isActive = entity.getActive();
        this.usagesCount = entity.getUsagesCount();
    }
}
