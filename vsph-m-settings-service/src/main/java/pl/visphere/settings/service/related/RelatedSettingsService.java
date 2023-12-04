/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.service.related;

import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;

public interface RelatedSettingsService {
    UserSettingsResDto getPersistedUserRelatedSettings(Long userId);
    void instantiateUserRelatedSettings(Long userId);
}
