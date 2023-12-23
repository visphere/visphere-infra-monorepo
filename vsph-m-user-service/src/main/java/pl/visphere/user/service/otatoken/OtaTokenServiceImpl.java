/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.service.otatoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.user.domain.otatoken.OtaTokenEntity;
import pl.visphere.user.domain.otatoken.OtaTokenRepository;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtaTokenServiceImpl implements OtaTokenService {
    private final Environment environment;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    @Transactional
    public GenerateOtaResDto generate(UserEntity user, OtaToken type) {
        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(10);
        } while (otaTokenRepository.existsByToken(token));

        final ZonedDateTime expiredDate = type.addTime(environment);
        final OtaTokenEntity otaToken = OtaTokenEntity.builder()
            .token(token)
            .type(type)
            .expiredAt(expiredDate)
            .build();

        user.persistOtaToken(otaToken);

        final GenerateOtaResDto resDto = GenerateOtaResDto.builder()
            .token(token)
            .expiredAt(expiredDate)
            .build();

        log.info("Successfully generated and saved ota token: '{}'.", resDto);
        return resDto;
    }

    @Override
    public boolean checkIfIsExpired(ZonedDateTime time) {
        return time.isBefore(ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
