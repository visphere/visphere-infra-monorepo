/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.otatoken;

import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.security.OtaToken;

public interface OtaTokenService {
    GenerateOtaResDto generate(UserEntity user, OtaToken type);
}
