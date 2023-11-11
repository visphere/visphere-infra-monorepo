/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface SecurityExtender {
    HttpSecurity extend(HttpSecurity security) throws Exception;
}
