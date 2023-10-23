/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private AppGrantedAuthority role;

    public AppGrantedAuthority getRole() {
        return role;
    }

    void setRole(AppGrantedAuthority role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "{" +
            "role=" + role +
            '}';
    }
}
