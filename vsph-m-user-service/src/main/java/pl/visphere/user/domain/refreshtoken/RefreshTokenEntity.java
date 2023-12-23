/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.domain.refreshtoken;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.user.domain.user.UserEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "refresh_tokens")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String refreshToken;

    private ZonedDateTime expiringAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public String getRefreshToken() {
        return refreshToken;
    }

    void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    ZonedDateTime getExpiringAt() {
        return expiringAt;
    }

    void setExpiringAt(ZonedDateTime expiredAt) {
        this.expiringAt = expiredAt;
    }

    UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "{" +
            "refreshToken=" + refreshToken +
            ", expiringAt=" + expiringAt +
            '}';
    }
}
