/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.auth.domain.blacklistjwt.BlackListJwtEntity;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;

    private String emailAddress;

    private String password;

    private String secondEmailAddress;

    private Boolean enabledMfa;

    @Column(insertable = false)
    private Boolean isActivated;

    @ManyToMany
    @JoinTable(name = "users_roles",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Set<BlackListJwtEntity> blackListJwts = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST })
    private RefreshTokenEntity refreshToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    String getSecondEmailAddress() {
        return secondEmailAddress;
    }

    public void setSecondEmailAddress(String secondEmailAddress) {
        this.secondEmailAddress = secondEmailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabledMfa() {
        return enabledMfa;
    }

    public void setEnabledMfa(Boolean enabledMfa) {
        this.enabledMfa = enabledMfa;
    }

    public Boolean getActivated() {
        return isActivated;
    }

    void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    Set<BlackListJwtEntity> getBlackListJwts() {
        return blackListJwts;
    }

    void setBlackListJwts(Set<BlackListJwtEntity> blackListJwts) {
        this.blackListJwts = blackListJwts;
    }

    RefreshTokenEntity getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshTokenEntity refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void addRole(RoleEntity roleEntity) {
        this.roles.add(roleEntity);
    }

    public void addExpiredJwt(BlackListJwtEntity blackListJwt) {
        this.blackListJwts.add(blackListJwt);
    }

    public void persistRefreshToken(RefreshTokenEntity refreshToken) {
        this.refreshToken = refreshToken;
        refreshToken.setUser(this);
    }

    @Override
    public String toString() {
        return "{" +
            "username=" + username +
            ", emailAddress=" + emailAddress +
            ", secondEmailAddress=" + secondEmailAddress +
            ", password=" + password +
            ", enabledMfa=" + enabledMfa +
            ", isActivated=" + isActivated +
            ", roles=" + roles +
            '}';
    }
}
