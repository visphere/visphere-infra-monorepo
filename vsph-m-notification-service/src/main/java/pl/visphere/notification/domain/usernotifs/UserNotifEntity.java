/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.domain.usernotifs;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "user_notifs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotifEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean isMailNotifsEnabled;

    private Long userId;

    public Boolean getMailNotifsEnabled() {
        return isMailNotifsEnabled;
    }

    public void setMailNotifsEnabled(Boolean mailNotifsEnabled) {
        isMailNotifsEnabled = mailNotifsEnabled;
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
            "isMailNotifsEnabled=" + isMailNotifsEnabled +
            ", userId=" + userId +
            '}';
    }
}
