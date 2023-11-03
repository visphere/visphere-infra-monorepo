/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guildlink;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.sphere.domain.guild.GuildEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "guild_links")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildLinkEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    private ZonedDateTime expiredAt;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guild;

    public String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(ZonedDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    GuildEntity getGuild() {
        return guild;
    }

    void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    @Override
    public String toString() {
        return "{" +
            "token=" + token +
            ", expiredAt=" + expiredAt +
            ", isActive=" + isActive +
            ", guild=" + guild +
            '}';
    }
}
