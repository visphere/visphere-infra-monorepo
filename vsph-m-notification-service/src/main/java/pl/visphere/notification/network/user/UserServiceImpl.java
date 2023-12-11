/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.notification.cache.CacheName;
import pl.visphere.notification.domain.usernotifs.UserNotifEntity;
import pl.visphere.notification.domain.usernotifs.UserNotifRepository;
import pl.visphere.notification.exception.UserNotifException;
import pl.visphere.notification.i18n.LocaleSet;
import pl.visphere.notification.network.user.dto.UserNotifSettingsResDto;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final I18nService i18nService;
    private final CacheService cacheService;

    private final UserNotifRepository userNotifRepository;

    @Override
    public UserNotifSettingsResDto getUserMailNotifsState(AuthUserDetails user) {
        final UserNotifEntity userNotif = cacheService
            .getSafetyFromCache(CacheName.USER_NOTIFS_USER_ID, user.getId(), UserNotifEntity.class,
                () -> userNotifRepository.findByUserId(user.getId()))
            .orElseThrow(() -> new UserNotifException.UserNotifNotFoundException(user.getId()));

        log.info("Successfully get account notifications settings: '{}'.", userNotif);
        return UserNotifSettingsResDto.builder()
            .isEmailNotifsEnabled(userNotif.getMailNotifsEnabled())
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto toggleUserMailNotifsState(boolean isEnabled, AuthUserDetails user) {
        final UserNotifEntity userNotif = userNotifRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new UserNotifException.UserNotifNotFoundException(user.getId()));

        userNotif.setMailNotifsEnabled(isEnabled);
        cacheService.deleteCache(CacheName.USER_NOTIFS_USER_ID, user.getId());

        log.info("User mail notification settings was toggled to: '{}' for user: '{}'.", isEnabled, user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.TOGGLE_USER_MAIL_NOTIF_STATE_RESPONSE_SUCCESS))
            .build();
    }
}
