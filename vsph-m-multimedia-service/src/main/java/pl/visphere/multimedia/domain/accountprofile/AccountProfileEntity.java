/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.domain.accountprofile;

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
@Table(name = "account_profiles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountProfileEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String profileColor;

    private String profileImageUuid;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    private Long userId;

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

    Long getUserId() {
        return userId;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "{" +
            "profileColor=" + profileColor +
            ", profileImageUuid=" + profileImageUuid +
            ", imageType=" + imageType +
            ", userId=" + userId +
            '}';
    }
}
