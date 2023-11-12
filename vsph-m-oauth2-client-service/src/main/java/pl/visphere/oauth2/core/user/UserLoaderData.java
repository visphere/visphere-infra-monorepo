/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.core.user;

import lombok.Builder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import pl.visphere.oauth2.core.OAuth2Supplier;

@Builder
public record UserLoaderData(
    OAuth2Supplier supplier,
    OidcIdToken oidcIdToken,
    OidcUserInfo oidcUserInfo
) {
}
