/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh;

import pl.moonsphere.auth.network.password_refresh.dto.AttemptReqDto;
import pl.moonsphere.auth.network.password_refresh.dto.ChangeReqDto;
import pl.moonsphere.lib.dto.BaseMessageResDto;
import pl.moonsphere.lib.dto.BaseVerificationResDto;

interface IPasswordRefreshService {
    BaseMessageResDto request(AttemptReqDto reqDto);
    BaseVerificationResDto verify(String token);
    BaseMessageResDto change(String token, ChangeReqDto reqDto);
}
