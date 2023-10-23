/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.blacklistjwt;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "blacklist_jwts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackListJwtEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String expiredJwt;

    private ZonedDateTime expiringAt;

    String getExpiredJwt() {
        return expiredJwt;
    }

    void setExpiredJwt(String expiredJwt) {
        this.expiredJwt = expiredJwt;
    }

    ZonedDateTime getExpiringAt() {
        return expiringAt;
    }

    void setExpiringAt(ZonedDateTime expiringAt) {
        this.expiringAt = expiringAt;
    }

    @Override
    public String toString() {
        return "{" +
            "expiredJwt=" + expiredJwt +
            ", expiringAt=" + expiringAt +
            '}';
    }
}
