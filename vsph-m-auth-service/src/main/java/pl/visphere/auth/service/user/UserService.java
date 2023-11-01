/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.user;

import pl.visphere.lib.kafka.payload.auth.CheckUserResDto;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;

public interface UserService {
    CheckUserResDto checkUser(String username);
    UserDetailsResDto getUserDetails(Long userId);
}
