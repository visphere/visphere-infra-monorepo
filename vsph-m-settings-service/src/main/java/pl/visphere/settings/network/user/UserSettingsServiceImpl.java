/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.network.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.settings.cache.CacheName;
import pl.visphere.settings.domain.userrelation.UserRelationModel;
import pl.visphere.settings.domain.userrelation.UserRelationRepository;
import pl.visphere.settings.i18n.LocaleSet;
import pl.visphere.settings.network.user.dto.RelatedValueReqDto;

@Slf4j
@Service
@RequiredArgsConstructor
class UserSettingsServiceImpl implements UserSettingsService {
    private final I18nService i18nService;
    private final CacheService cacheService;

    private final UserRelationRepository userRelationRepository;

    @Override
    public BaseMessageResDto relateLangWithUser(RelatedValueReqDto reqDto, AuthUserDetails user) {
        final UserRelationModel relation = getUserRelationModel(user);

        final LocaleSet responseMessage = reqDto.getRelatedValue() != null
            ? LocaleSet.PERSISTED_RELATED_LANG_RESPONSE_SUCCESS
            : LocaleSet.REMOVE_RELATED_LANG_RESPONSE_SUCCESS;

        relation.setLang(reqDto.getRelatedValue());
        saveAndUpdateCache(user, relation);

        log.info("Successfully updated related lang for user: '{}'", relation);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(responseMessage))
            .build();
    }

    @Override
    public BaseMessageResDto relateThemeWithUser(RelatedValueReqDto reqDto, AuthUserDetails user) {
        final UserRelationModel relation = getUserRelationModel(user);

        final LocaleSet responseMessage = reqDto.getRelatedValue() != null
            ? LocaleSet.PERSISTED_RELATED_THEME_RESPONSE_SUCCESS
            : LocaleSet.REMOVE_RELATED_THEME_RESPONSE_SUCCESS;

        relation.setTheme(reqDto.getRelatedValue());
        saveAndUpdateCache(user, relation);

        log.info("Successfully updated related theme for user: '{}'", relation);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(responseMessage))
            .build();
    }

    @Override
    public BaseMessageResDto updatePushNotificationsSettings(boolean isEnabled, AuthUserDetails user) {
        final UserRelationModel relation = getUserRelationModel(user);

        relation.setPushNotifsEnabled(isEnabled);
        if (!isEnabled) {
            relation.setPushNotifsSoundEnabled(false);
        }
        saveAndUpdateCache(user, relation);

        log.info("Successfully updated push notifications settings for user: '{}'", relation);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.PUSH_NOTIFICATIONS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto updatePushNotificationsSoundSettings(boolean isEnabled, AuthUserDetails user) {
        final UserRelationModel relation = getUserRelationModel(user);

        if (isEnabled) {
            relation.setPushNotifsEnabled(true);
        }
        relation.setPushNotifsSoundEnabled(isEnabled);
        saveAndUpdateCache(user, relation);

        log.info("Successfully updated push notifications sound settings for user: '{}'", relation);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.PUSH_NOTIFICATIONS_SOUND_RESPONSE_SUCCESS))
            .build();
    }

    private UserRelationModel getUserRelationModel(AuthUserDetails user) {
        return userRelationRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getId()));
    }

    private void saveAndUpdateCache(AuthUserDetails user, UserRelationModel relation) {
        final UserRelationModel saved = userRelationRepository.save(relation);
        cacheService.updateCache(CacheName.USER_RELATION_MODEL_USER_ID, user.getId(), saved);
    }
}
