/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.kafka;

import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.lib.kafka.payload.UserDetailsResDto;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;

@Component
class AuthKafkaMapper {
    UserDetailsResDto mapToUserDetailsResDto(UserEntity user, Set<AppGrantedAuthority> roles) {
        return UserDetailsResDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .isNonLocked(true)
            .authorities(roles)
            .isEnabled(true)
            .build();
    }
}
