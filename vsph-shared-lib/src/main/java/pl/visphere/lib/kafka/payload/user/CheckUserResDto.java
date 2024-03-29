/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.kafka.payload.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserResDto {
    private Long id;
    private String username;
    private String password;
    private String emailAddress;
    private String secondEmailAddress;
    private boolean enabledMfa;
    private boolean isDisabled;
    private boolean isActivated;
    private Set<AppGrantedAuthority> authorities;
}
