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
import pl.visphere.auth.domain.mfauser.MfaUserEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    @Column(insertable = false)
    private Boolean isActivated;

    @Column(insertable = false)
    private Boolean isDisabled;

    private Boolean externalCredProvider;

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "users_roles",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "user")
    private Set<BlackListJwtEntity> blackListJwts = new HashSet<>();

    @OneToOne(cascade = { PERSIST, MERGE }, mappedBy = "user")
    private MfaUserEntity mfaUser;

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "user")
    private Set<OtaTokenEntity> otaTokens = new HashSet<>();

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "user")
    private Set<RefreshTokenEntity> refreshTokens = new HashSet<>();

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

    public String getSecondEmailAddress() {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public Boolean getExternalCredProvider() {
        return externalCredProvider;
    }

    public void setExternalCredProvider(Boolean externalCredProvider) {
        this.externalCredProvider = externalCredProvider;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public void addRole(RoleEntity roleEntity) {
        this.roles.add(roleEntity);
    }

    public MfaUserEntity getMfaUser() {
        return mfaUser;
    }

    public void setMfaUser(MfaUserEntity mfaUser) {
        this.mfaUser = mfaUser;
    }

    Set<BlackListJwtEntity> getBlackListJwts() {
        return blackListJwts;
    }

    void setBlackListJwts(Set<BlackListJwtEntity> blackListJwts) {
        this.blackListJwts = blackListJwts;
    }

    Set<OtaTokenEntity> getOtaTokens() {
        return otaTokens;
    }

    void setOtaTokens(Set<OtaTokenEntity> otaTokens) {
        this.otaTokens = otaTokens;
    }

    Set<RefreshTokenEntity> getRefreshTokens() {
        return refreshTokens;
    }

    void setRefreshTokens(Set<RefreshTokenEntity> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    public void persistBlackListJwt(BlackListJwtEntity blackListJwt) {
        blackListJwts.add(blackListJwt);
        blackListJwt.setUser(this);
    }

    public void persistMfaUser(MfaUserEntity mfaUser) {
        this.mfaUser = mfaUser;
        mfaUser.setUser(this);
    }

    public void persistOtaToken(OtaTokenEntity otaToken) {
        otaTokens.add(otaToken);
        otaToken.setUser(this);
    }

    public void persistRefreshToken(RefreshTokenEntity refreshToken) {
        refreshTokens.add(refreshToken);
        refreshToken.setUser(this);
    }

    @Override
    public String toString() {
        return "{" +
            "username=" + username +
            ", emailAddress=" + emailAddress +
            ", secondEmailAddress=" + secondEmailAddress +
            ", firstName=" + firstName +
            ", lastName=" + lastName +
            ", birthDate=" + birthDate +
            ", isActivated=" + isActivated +
            ", isDisabled=" + isDisabled +
            ", externalCredProvider=" + externalCredProvider +
            '}';
    }
}
