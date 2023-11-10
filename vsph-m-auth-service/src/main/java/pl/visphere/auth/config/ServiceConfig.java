/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.visphere.auth.service.mfa.UtcTimeProvider;
import pl.visphere.lib.AbstractBaseServiceBeans;

@Configuration
class ServiceConfig extends AbstractBaseServiceBeans {
    @Bean
    SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

    @Bean
    QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator();
    }

    @Bean
    CodeVerifier codeVerifier() {
        final CodeGenerator codeGenerator = new DefaultCodeGenerator();
        final TimeProvider timeProvider = new UtcTimeProvider();
        return new DefaultCodeVerifier(codeGenerator, timeProvider);
    }
}
