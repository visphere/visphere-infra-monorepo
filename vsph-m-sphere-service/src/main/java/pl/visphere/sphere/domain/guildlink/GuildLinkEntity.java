/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guildlink;

import jakarta.persistence.*;
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

    private String name;

    private String token;

    private ZonedDateTime expiredAt;

    private Boolean isActive;

    @Column(insertable = false)
    private Long usagesCount;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Long getUsagesCount() {
        return usagesCount;
    }

    public void setUsagesCount(Long usagesCount) {
        this.usagesCount = usagesCount;
    }

    public GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", token=" + token +
            ", expiredAt=" + expiredAt +
            ", isActive=" + isActive +
            ", usagesCount=" + usagesCount +
            '}';
    }
}
