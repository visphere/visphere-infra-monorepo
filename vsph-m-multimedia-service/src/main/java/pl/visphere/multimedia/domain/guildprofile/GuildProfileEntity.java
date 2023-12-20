/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.domain.guildprofile;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.multimedia.domain.ImageType;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "guild_profiles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildProfileEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String profileColor;

    private String profileImageUuid;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    private Long guildId;

    public String getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }

    public String getProfileImageUuid() {
        return profileImageUuid;
    }

    public void setProfileImageUuid(String profileImageUuid) {
        this.profileImageUuid = profileImageUuid;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Long getGuildId() {
        return guildId;
    }

    void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    @Override
    public String toString() {
        return "{" +
            "profileColor=" + profileColor +
            ", profileImageUuid=" + profileImageUuid +
            ", imageType=" + imageType +
            ", guildId=" + guildId +
            '}';
    }
}
