/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.StatelesslessUserDetailsService;

public abstract class AbstractSecurityConfigBeans {
    @Bean
    StatelesslessUserDetailsService statelesslessUserDetailsService(SyncQueueHandler syncQueueHandler) {
        return new StatelesslessUserDetailsService(syncQueueHandler);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
