/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.domain.userrelation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "user_relations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationModel extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String lang;

    private String theme;

    @Column(insertable = false)
    private Boolean pushNotifsEnabled;

    @Column(insertable = false)
    private Boolean pushNotifsSoundEnabled;

    private Long userId;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getPushNotifsEnabled() {
        return pushNotifsEnabled;
    }

    public void setPushNotifsEnabled(Boolean pushNotifsEnabled) {
        this.pushNotifsEnabled = pushNotifsEnabled;
    }

    public Boolean getPushNotifsSoundEnabled() {
        return pushNotifsSoundEnabled;
    }

    public void setPushNotifsSoundEnabled(Boolean pushNotifsSoundEnabled) {
        this.pushNotifsSoundEnabled = pushNotifsSoundEnabled;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "{" +
            "lang=" + lang +
            ", theme=" + theme +
            ", pushNotifsEnabled=" + pushNotifsEnabled +
            ", pushNotifsSoundEnabled=" + pushNotifsSoundEnabled +
            ", userId=" + userId +
            '}';
    }
}
