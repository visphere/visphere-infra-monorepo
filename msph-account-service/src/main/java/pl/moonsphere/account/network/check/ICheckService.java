/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import pl.moonsphere.account.network.check.dto.CheckUsernameExist;

interface ICheckService {
    CheckUsernameExist checkIfUsernameAlreadyExist(String username);
}
