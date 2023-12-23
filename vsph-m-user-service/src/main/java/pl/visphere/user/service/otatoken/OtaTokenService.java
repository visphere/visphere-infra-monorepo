/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.service.otatoken;

import pl.visphere.lib.security.OtaToken;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;

import java.time.ZonedDateTime;

public interface OtaTokenService {
    GenerateOtaResDto generate(UserEntity user, OtaToken type);
    boolean checkIfIsExpired(ZonedDateTime time);
}
