/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.moonsphere.account.network.check.dto.CheckUsernameExist;

@Service
@RequiredArgsConstructor
public class CheckService implements ICheckService {
    @Override
    public CheckUsernameExist checkIfUsernameAlreadyExist(String username) {
        // check if passed username is already declared in system

        return CheckUsernameExist.builder()
            .alreadyExist(true)
            .build();
    }
}
