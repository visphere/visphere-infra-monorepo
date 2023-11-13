/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.domain.oauth2user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.oauth2.core.OAuth2Supplier;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "oauth2_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private OAuth2Supplier supplier;

    private String providerId;

    private String profileImageUrl;

    @Column(insertable = false)
    private Boolean providerImageSelected;

    private Long userId;

    public OAuth2Supplier getSupplier() {
        return supplier;
    }

    void setSupplier(OAuth2Supplier supplier) {
        this.supplier = supplier;
    }

    public String getProviderId() {
        return providerId;
    }

    void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getProviderImageSelected() {
        return providerImageSelected;
    }

    void setProviderImageSelected(Boolean providerImageSelected) {
        this.providerImageSelected = providerImageSelected;
    }

    public Long getUserId() {
        return userId;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "{" +
            "supplier=" + supplier +
            ", providerId=" + providerId +
            ", profileImageUrl=" + profileImageUrl +
            ", providerImageSelected=" + providerImageSelected +
            ", userId=" + userId +
            '}';
    }
}
