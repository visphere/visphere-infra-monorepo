/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.otatoken;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.lib.security.OtaToken;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ota_tokens")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtaTokenEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    private ZonedDateTime expiredAt;

    @Column(insertable = false)
    private Boolean isUsed;

    @Enumerated(EnumType.STRING)
    private OtaToken type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    ZonedDateTime getExpiredAt() {
        return expiredAt;
    }

    void setExpiredAt(ZonedDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    OtaToken getType() {
        return type;
    }

    void setType(OtaToken type) {
        this.type = type;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "OtaTokenEntity{" +
            "token=" + token +
            ", expiredAt=" + expiredAt +
            ", isUsed=" + isUsed +
            ", type=" + type +
            '}';
    }
}
