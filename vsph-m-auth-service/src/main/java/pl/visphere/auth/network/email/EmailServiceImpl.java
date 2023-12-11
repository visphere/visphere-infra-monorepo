/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.visphere.auth.cache.CacheName;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.AccountException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.email.dto.EmailAddressDto;
import pl.visphere.auth.network.email.dto.EmailAddressReqDto;
import pl.visphere.auth.network.email.dto.SecondEmailAddressReqDto;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AuthUserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
class EmailServiceImpl implements EmailService {
    private final I18nService i18nService;
    private final AsyncQueueHandler asyncQueueHandler;
    private final CacheService cacheService;
    private final OtaTokenService otaTokenService;
    private final EmailMapper emailMapper;
    private final SyncQueueHandler syncQueueHandler;

    private final UserRepository userRepository;

    @Override
    public BaseMessageResDto requestUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = commonRequestUpdateFirstEmailAddress(reqDto, user);
        log.info("Successfully invoke request for change email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.REQUEST_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto requestResendUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = commonRequestUpdateFirstEmailAddress(reqDto, user);
        log.info("Successfully invoke resend request for change email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_REQUEST_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto requestUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = commonRequestUpdateSecondEmailAddress(reqDto, user);
        log.info("Successfully invoke request for change second email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.REQUEST_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto requestResendUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = commonRequestUpdateSecondEmailAddress(reqDto, user);
        log.info("Successfully invoke resend request for change email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_REQUEST_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto updateFirstEmailAddress(EmailAddressReqDto reqDto, String token, AuthUserDetails user) {
        final UserEntity userEntity = getUser(user, "update email address");
        final String email = reqDto.getEmailAddress();
        checkIfUserWithSameEmailExist(reqDto, user.getId());

        userEntity.setEmailAddress(email);
        saveAndUpdateCache(userEntity);

        sendAsyncEmail(userEntity, email, QueueTopic.EMAIL_CHANGED_EMAIL);

        log.info("Successfully validate token and change email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.UPDATE_FIRST_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto updateSecondEmailAddress(
        SecondEmailAddressReqDto reqDto, String token, AuthUserDetails user
    ) {
        final UserEntity userEntity = getUser(user, "update second email address");
        userEntity.setSecondEmailAddress(reqDto.getEmailAddress());
        saveAndUpdateCache(userEntity);

        sendAsyncEmail(userEntity, userEntity.getEmailAddress(), QueueTopic.EMAIL_CHANGED_SECOND_EMAIL);

        log.info("Successfully validate token and change second email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.UPDATE_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto deleteSecondEmailAddress(AuthUserDetails user) {
        final UserEntity userEntity = getUser(user, "delete second email address");
        userEntity.setSecondEmailAddress(null);
        saveAndUpdateCache(userEntity);

        sendAsyncEmail(userEntity, userEntity.getEmailAddress(), QueueTopic.EMAIL_REMOVED_SECOND_EMAIL);

        log.info("Successfully removed second email address for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.REMOVE_SECOND_EMAIL_ADDRESS_RESPONSE_SUCCESS))
            .build();
    }

    private UserEntity getUser(AuthUserDetails user, String action) {
        final UserEntity userEntity = userRepository
            .findById(user.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getId()));
        if (userRepository.existsByIdAndExternalCredProviderIsTrue(user.getId())) {
            throw new AccountException.ImmutableValueException(action);
        }
        return userEntity;
    }

    private UserEntity commonRequestUpdateFirstEmailAddress(EmailAddressDto dto, AuthUserDetails user) {
        final UserEntity userEntity = getUser(user, "request for update email address");
        checkIfUserWithSameEmailExist(dto, user.getId());

        generateOtaAndsendAsyncEmail(userEntity, dto, OtaToken.CHANGE_EMAIL,
            QueueTopic.EMAIL_REQ_CHANGE_EMAIL);
        return userEntity;
    }

    private UserEntity commonRequestUpdateSecondEmailAddress(SecondEmailAddressReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = getUser(user, "request for update second email address");

        generateOtaAndsendAsyncEmail(userEntity, reqDto, OtaToken.CHANGE_SECOND_EMAIL,
            QueueTopic.EMAIL_REQ_CHANGE_SECOND_EMAIL);
        return userEntity;
    }

    private void saveAndUpdateCache(UserEntity userEntity) {
        final UserEntity saved = userRepository.save(userEntity);
        cacheService.updateCache(CacheName.USER_ENTITY_USER_ID, userEntity.getId(), saved);
    }

    private void generateOtaAndsendAsyncEmail(UserEntity user, EmailAddressDto dto, OtaToken otaType, QueueTopic kafkaTopic) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, otaType);
        final SendTokenEmailReqDto emailReqDto = emailMapper
            .mapToSendTokenEmailReq(user, dto.getEmailAddress(), otaResDto, profileImageDetails);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(kafkaTopic, emailReqDto);
    }

    private void sendAsyncEmail(UserEntity user, String emailAddress, QueueTopic kafkaTopic) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(), ProfileImageDetailsResDto.class);

        final SendBaseEmailReqDto emailReqDto = emailMapper
            .mapToSendBaseEmailReq(user, emailAddress, profileImageDetails);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(kafkaTopic, emailReqDto);
    }

    private void checkIfUserWithSameEmailExist(EmailAddressDto dto, Long id) {
        final String email = dto.getEmailAddress();
        if (userRepository.existsByEmailAddressAndIdIsNot(email, id)) {
            throw new UserException.UserAlreadyExistException(email);
        }
    }
}
