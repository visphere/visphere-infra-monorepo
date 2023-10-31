/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
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

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private Boolean enabledMfa;

    @Column(insertable = false)
    private Boolean isActivated;

    @ManyToMany
    @JoinTable(name = "users_roles",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<RoleEntity> roles = new HashSet<>();

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

    LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public void setActivated(Boolean activated) {
        isActivated = activated;
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

    @Override
    public String toString() {
        return "{" +
            "username=" + username +
            ", emailAddress=" + emailAddress +
            ", password=" + password +
            ", secondEmailAddress=" + secondEmailAddress +
            ", firstName=" + firstName +
            ", lastName=" + lastName +
            ", birthDate=" + birthDate +
            ", enabledMfa=" + enabledMfa +
            ", isActivated=" + isActivated +
            '}';
    }
}