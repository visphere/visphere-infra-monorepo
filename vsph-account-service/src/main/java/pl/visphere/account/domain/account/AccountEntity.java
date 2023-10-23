/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;

    private String lastName;

    private String defaultColor;

    private LocalDate birthDate;

    private Boolean allowNotifs;

    private Long userId;

    public String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getDefaultColor() {
        return defaultColor;
    }

    void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    LocalDate getBirthDate() {
        return birthDate;
    }

    void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    Boolean getAllowNotifs() {
        return allowNotifs;
    }

    void setAllowNotifs(Boolean allowNotifs) {
        this.allowNotifs = allowNotifs;
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
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", defaultColor=" + defaultColor +
            ", birthDate=" + birthDate +
            ", allowNotifs=" + allowNotifs +
            ", userId=" + userId +
            '}';
    }
}
