/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.service.related;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.kafka.payload.settings.UserSettingsResDto;
import pl.visphere.settings.cache.CacheName;
import pl.visphere.settings.domain.userrelation.UserRelationModel;
import pl.visphere.settings.domain.userrelation.UserRelationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
class RelatedSettingsServiceImpl implements RelatedSettingsService {
    private final CacheService cacheService;
    private final ModelMapper modelMapper;

    private final UserRelationRepository userRelationRepository;

    @Override
    public UserSettingsResDto getPersistedUserRelatedSettings(Long userId) {
        final UserRelationModel userRelation = cacheService
            .getSafetyFromCache(CacheName.USER_RELATION_MODEL_USER_ID, userId, UserRelationModel.class,
                () -> userRelationRepository.findByUserId(userId))
            .orElseThrow(() -> new UserException.UserNotExistException(userId));

        final UserSettingsResDto resDto = modelMapper.map(userRelation, UserSettingsResDto.class);
        log.info("Successfully found user relation settings: '{}'.", resDto);
        return resDto;
    }

    @Override
    public void instantiateUserRelatedSettings(Long userId) {
        if (userRelationRepository.existsByUserId(userId)) {
            log.info("User relation model already exist. Skipping instantiate procedure.");
            return;
        }
        final UserRelationModel userRelationModel = UserRelationModel.builder()
            .userId(userId)
            .build();

        userRelationRepository.save(userRelationModel);
        log.info("Successfully instantiated user relation model entity: '{}'.", userRelationModel);
    }

    @Override
    @Transactional
    public void deleteUserSettingsData(Long userId) {
        userRelationRepository.deleteByUserId(userId);
        log.info("Successfully deleted correlated settings with user with ID: '{}'.", userId);
    }
}
