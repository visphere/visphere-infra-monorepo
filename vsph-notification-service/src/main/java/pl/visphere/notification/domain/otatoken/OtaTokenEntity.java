/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.domain.otatoken;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ota_tokens")
@NoArgsConstructor
public class OtaTokenEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    private ZonedDateTime expiredAt;

    @Column(insertable = false)
    private Boolean isUsed;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private Long userId;

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

    void setUsed(Boolean used) {
        isUsed = used;
    }

    TokenType getType() {
        return type;
    }

    void setType(TokenType type) {
        this.type = type;
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
            "token=" + token +
            ", expiredAt=" + expiredAt +
            ", isUsed=" + isUsed +
            ", type=" + type +
            ", userId=" + userId +
            '}';
    }
}
