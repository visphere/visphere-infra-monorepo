/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import pl.visphere.lib.exception.GenericRestException;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum OAuth2Supplier {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    ;

    private final String supplierName;

    public static OAuth2Supplier checkIfSupplierExist(OAuth2UserRequest req, List<String> suppliers) {
        final String rawName = req.getClientRegistration().getClientName();
        return Stream.of(values())
            .filter(s -> s.supplierName.equalsIgnoreCase(rawName) && suppliers.contains(s.supplierName))
            .findFirst()
            .orElseThrow(() -> {
                log.error("Unable to find supplier: '{}'. Supplier is not handled by OAuth2 client.", rawName);
                return new GenericRestException();
            });
    }
}
