/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.guildlink.dto;

import lombok.Getter;
import pl.visphere.sphere.domain.guildlink.GuildLinkEntity;

import java.time.ZonedDateTime;

@Getter
public class GuildLinkResDto {
    private final Long id;
    private final String urlPrefix;
    private final String token;
    private final ZonedDateTime expiredAt;
    private final boolean isActive;

    public GuildLinkResDto(GuildLinkEntity entity, String urlPrefix) {
        this.id = entity.getId();
        this.urlPrefix = urlPrefix;
        this.token = entity.getToken();
        this.expiredAt = entity.getExpiredAt();
        this.isActive = entity.getActive();
    }
}
