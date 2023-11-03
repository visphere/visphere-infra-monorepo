/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guild;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "guilds")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    @Enumerated(EnumType.STRING)
    private GuildCategory category;

    private Boolean isPrivate;

    private Long ownerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuildCategory getCategory() {
        return category;
    }

    public void setCategory(GuildCategory category) {
        this.category = category;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    Long getOwnerId() {
        return ownerId;
    }

    void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", category=" + category +
            ", isPrivate=" + isPrivate +
            ", ownerId=" + ownerId +
            '}';
    }
}
