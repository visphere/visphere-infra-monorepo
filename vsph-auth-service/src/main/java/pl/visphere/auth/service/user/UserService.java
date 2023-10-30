/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.user;

import pl.visphere.lib.kafka.payload.auth.*;

public interface UserService {
    CreateUserResDto createUser(CreateUserReqDto reqDto);
    ActivateUserResDto activateUser(ActivateUserReqDto reqDto);
    CheckUserResDto checkUser(String username);
}
