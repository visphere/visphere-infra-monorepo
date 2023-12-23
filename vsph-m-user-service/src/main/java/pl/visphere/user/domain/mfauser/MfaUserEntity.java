/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.domain.mfauser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.user.domain.user.UserEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "mfa_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaUserEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(insertable = false)
    private Boolean mfaIsSetup;

    private String mfaSecret;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Boolean getMfaIsSetup() {
        return mfaIsSetup;
    }

    public void setMfaIsSetup(Boolean mfaIsSetup) {
        this.mfaIsSetup = mfaIsSetup;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
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
            "mfaIsSetup=" + mfaIsSetup +
            '}';
    }
}
