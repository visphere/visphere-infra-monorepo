/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.service.usernotif;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.kafka.payload.notification.PersistUserNotifSettingsReqDto;
import pl.visphere.notification.domain.usernotifs.UserNotifEntity;
import pl.visphere.notification.domain.usernotifs.UserNotifRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotifServiceImpl implements UserNotifService {
    private final UserNotifRepository userNotifRepository;

    @Override
    public void persistUserNotifSettings(PersistUserNotifSettingsReqDto reqDto) {
        if (userNotifRepository.existsByUserId(reqDto.userId())) {
            log.info("User notifications settings already exist. Skipping.");
        }
        final UserNotifEntity userNotif = UserNotifEntity.builder()
            .userId(reqDto.userId())
            .isMailNotifsEnabled(reqDto.isEmailNotifsEnabled())
            .build();
        userNotifRepository.save(userNotif);

        log.info("Successfully persisted user notification settings.");
    }

    @Override
    @Transactional
    public void deleteNotifUserSettings(Long userId) {
        userNotifRepository.deleteByUserId(userId);
        log.info("Successfully deleted correlated notification settings with user with ID: '{}'.", userId);
    }
}
