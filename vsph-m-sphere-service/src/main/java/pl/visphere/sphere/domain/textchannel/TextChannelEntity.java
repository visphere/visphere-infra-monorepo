/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.textchannel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.sphere.domain.guild.GuildEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "text_channels")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextChannelEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "guild_id")
    private GuildEntity guild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            ", guild=" + guild +
            '}';
    }
}
