/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.auth.network.password_refresh;

import pl.moonsphere.auth.network.password_refresh.dto.AttemptReqDto;
import pl.moonsphere.auth.network.password_refresh.dto.ChangeReqDto;
import pl.moonsphere.lib.BaseMessageResDto;

interface IPasswordRefreshService {
    BaseMessageResDto request(AttemptReqDto reqDto);
    BaseMessageResDto verify(String token);
    BaseMessageResDto resend(AttemptReqDto reqDto);
    BaseMessageResDto change(String token, ChangeReqDto reqDto);
}
