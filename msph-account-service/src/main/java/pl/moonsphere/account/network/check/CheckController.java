/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.account.network.check;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.moonsphere.account.network.check.dto.CheckUsernameExist;

@RestController
@RequestMapping("/api/v1/account/check")
@RequiredArgsConstructor
class CheckController {
    private final ICheckService checkService;
    
    @GetMapping("/username/{username}/exist")
    ResponseEntity<CheckUsernameExist> checkIfUsernameAlreadyExist(@PathVariable String username) {
        return ResponseEntity.ok(checkService.checkIfUsernameAlreadyExist(username));
    }
}
