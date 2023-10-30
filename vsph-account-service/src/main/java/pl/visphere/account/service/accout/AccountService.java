/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.account.service.accout;

import pl.visphere.lib.kafka.payload.account.AccountDetailsResDto;

public interface AccountService {
    AccountDetailsResDto getAccountDetails(Long userId);
}
