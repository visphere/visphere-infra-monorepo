/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.otatoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.security.OtaToken;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtaTokenServiceImpl implements OtaTokenService {
    private final Environment environment;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    public GenerateOtaResDto generate(UserEntity user, OtaToken type) {
        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(10);
        } while (otaTokenRepository.existsByToken(token));

        final ZonedDateTime expiredDate = type.addTime(environment);
        final OtaTokenEntity otaToken = OtaTokenEntity.builder()
            .token(token)
            .type(type)
            .user(user)
            .expiredAt(expiredDate)
            .build();

        otaTokenRepository.save(otaToken);

        final GenerateOtaResDto resDto = GenerateOtaResDto.builder()
            .token(token)
            .expiredAt(expiredDate)
            .build();

        log.info("Successfully generated and saved ota token: '{}'", resDto);
        return resDto;
    }

    @Override
    public boolean checkIfIsExpired(ZonedDateTime time) {
        return time.isBefore(ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
