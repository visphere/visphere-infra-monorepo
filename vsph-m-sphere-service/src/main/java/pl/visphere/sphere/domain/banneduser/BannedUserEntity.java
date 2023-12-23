/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.banneduser;

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

@Entity
@Table(name = "banned_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannedUserEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildEntity guild;

    public Long getUserId() {
        return userId;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    @Override
    public String toString() {
        return "BannedUserEntity{" +
            "userId=" + userId +
            ", guildId=" + guild.getId() +
            '}';
    }
}
