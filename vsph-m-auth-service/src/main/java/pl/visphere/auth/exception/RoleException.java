/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.AbstractRestException;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Map;

public class RoleException {
    @Slf4j
    public static class RoleNotExistException extends AbstractRestException {
        public RoleNotExistException(AppGrantedAuthority role) {
            super(HttpStatus.NOT_FOUND, LibLocaleSet.ROLE_NOT_FOUND_EXCEPTION_MESSAGE, Map.of("role", role.getAuthority()));
            log.error("Searching role: '{}' not found in database.", role);
        }
    }
}
