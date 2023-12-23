/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

public interface SecurityBeanProvider {
    String[] securityEntrypointMatchers();
    default String[] unsecureMatchers() {
        return new String[]{};
    }
}
