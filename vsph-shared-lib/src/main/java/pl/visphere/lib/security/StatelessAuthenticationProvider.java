/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class StatelessAuthenticationProvider extends DaoAuthenticationProvider {
    public StatelessAuthenticationProvider(
        MessageSource messageSource,
        PasswordEncoder passwordEncoder,
        UserDetailsService userDetailsService
    ) {
        setMessageSource(messageSource);
        setPasswordEncoder(passwordEncoder);
        setUserDetailsService(userDetailsService);
    }

    public AuthenticationManager createManager() {
        return new ProviderManager(this);
    }
}
