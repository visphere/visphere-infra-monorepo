/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.service.oauth2service;

import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;

public interface OAuth2Service {
    OAuth2DetailsResDto getOAuthDetails(Long userId);
}
