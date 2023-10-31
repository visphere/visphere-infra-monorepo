/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.domain.accountprofile;

import jakarta.persistence.Entity;
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

    private Long userId;

    public String getProfileColor() {
        return profileColor;
    }

    void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }

    public String getProfileImageUuid() {
        return profileImageUuid;
    }

    void setProfileImageUuid(String profileImageUuid) {
        this.profileImageUuid = profileImageUuid;
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
            ", userId=" + userId +
            '}';
    }
}
