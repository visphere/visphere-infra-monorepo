/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.account;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.chat.DeleteUserMessagesReqDto;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.multimedia.UpdateUserProfileReqDto;
import pl.visphere.lib.kafka.payload.notification.PersistUserNotifSettingsReqDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.sphere.UserTextChannelsResDto;
import pl.visphere.lib.kafka.payload.user.CredentialsConfirmationReqDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AppGrantedAuthority;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.cache.CacheName;
import pl.visphere.user.domain.mfauser.MfaUserEntity;
import pl.visphere.user.domain.otatoken.OtaTokenEntity;
import pl.visphere.user.domain.otatoken.OtaTokenRepository;
import pl.visphere.user.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.user.domain.role.RoleEntity;
import pl.visphere.user.domain.role.RoleRepository;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.domain.user.UserRepository;
import pl.visphere.user.exception.AccountException;
import pl.visphere.user.exception.OtaTokenException;
import pl.visphere.user.exception.RoleException;
import pl.visphere.user.i18n.LocaleSet;
import pl.visphere.user.network.account.dto.*;
import pl.visphere.user.service.mfa.MfaProxyService;
import pl.visphere.user.service.otatoken.OtaTokenService;
import pl.visphere.user.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.user.service.user.UserService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {
    private final I18nService i18nService;
    private final SyncQueueHandler syncQueueHandler;
    private final ModelMapper modelMapper;
    private final AccountMapper accountMapper;
    private final AsyncQueueHandler asyncQueueHandler;
    private final PasswordEncoder passwordEncoder;
    private final OtaTokenService otaTokenService;
    private final MfaProxyService mfaProxyService;
    private final CacheService cacheService;
    private final JwtService jwtService;
    private final UserService userService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtaTokenRepository otaTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AccountDetailsResDto getAccountDetails(AuthUserDetails user) {
        final UserEntity userEntity = cacheService
            .getSafetyFromCache(CacheName.USER_ENTITY_USER_ID, user.getId(), UserEntity.class,
                () -> userRepository.findById(user.getId()))
            .orElseThrow(() -> new UserException.UserNotExistException(user.getId()));

        final AccountDetailsResDto resDto = accountMapper.mapToAccountDetailsRes(userEntity);
        log.info("Successfully find and fetched user account details: '{}'.", resDto);
        return resDto;
    }

    @Override
    @Transactional
    public UpdateAccountDetailsResDto updateAccountDetails(UpdateAccountDetailsReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = userRepository
            .findById(user.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getId()));
        final String username = reqDto.getUsername();

        if (userRepository.existsByUsernameAndIdIsNot(username, user.getId())) {
            throw new UserException.UserAlreadyExistException(username);
        }
        userEntity.setUsername(reqDto.getUsername());
        userEntity.setFirstName(StringUtils.capitalize(reqDto.getFirstName()));
        userEntity.setLastName(StringUtils.capitalize(reqDto.getLastName()));
        userEntity.setBirthDate(parseToLocalDate(reqDto.getBirthDate()));

        final UpdateUserProfileReqDto profileReqDto = UpdateUserProfileReqDto.builder()
            .initials(new char[]{ reqDto.getFirstName().charAt(0), reqDto.getLastName().charAt(0) })
            .userId(user.getId())
            .username(reqDto.getUsername())
            .build();

        final ProfileImageDetailsResDto resDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.UPDATE_DEFAULT_USER_PROFILE, profileReqDto,
                ProfileImageDetailsResDto.class);

        final TokenData tokenData = jwtService
            .generateAccessToken(user.getId(), reqDto.getUsername(), user.getEmailAddress());

        cacheService.deleteCache(CacheName.USER_ENTITY_USER_ID, userEntity.getId());

        log.info("Successfully updated user account details for user: '{}'.", userEntity);
        return UpdateAccountDetailsResDto.builder()
            .message(i18nService.getMessage(LocaleSet.UDPATE_ACCOUNT_DETAILS_RESPONSE_SUCCESS))
            .accessToken(tokenData.token())
            .profileImagePath(resDto.getProfileImagePath())
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto createNew(CreateAccountReqDto reqDto) {
        if (userRepository.existsByUsernameOrEmailAddress(reqDto.getUsername(), reqDto.getEmailAddress())) {
            throw new UserException.UserAlreadyExistException(reqDto.getUsername(), reqDto.getEmailAddress());
        }
        final RoleEntity role = roleRepository
            .findByRole(AppGrantedAuthority.USER)
            .orElseThrow(() -> new RoleException.RoleNotExistException(AppGrantedAuthority.USER));

        final UserEntity user = modelMapper.map(reqDto, UserEntity.class);
        if (reqDto.getSecondEmailAddress().equals(StringUtils.EMPTY)) {
            user.setSecondEmailAddress(null);
        }
        user.setFirstName(StringUtils.capitalize(reqDto.getFirstName()));
        user.setLastName(StringUtils.capitalize(reqDto.getLastName()));
        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
        user.setBirthDate(parseToLocalDate(reqDto.getBirthDate()));
        user.addRole(role);
        user.setExternalCredProvider(false);

        if (reqDto.getEnabledMfa()) {
            user.persistMfaUser(new MfaUserEntity());
            log.info("Successfully addded MFA user details user: '{}'.", user);
        }
        final UserEntity savedUser = userRepository.save(user);

        final PersistUserNotifSettingsReqDto notifSettingsReqDto = PersistUserNotifSettingsReqDto.builder()
            .userId(savedUser.getId())
            .isEmailNotifsEnabled(reqDto.getAllowNotifs())
            .build();
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.PERSIST_NOTIF_USER_SETTINGS, notifSettingsReqDto);

        final GenerateOtaResDto otaResDto = otaTokenService.generate(savedUser, OtaToken.ACTIVATE_ACCOUNT);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_ACTIVATE_ACCOUNT, accountMapper
            .mapToSendTokenEmailReq(reqDto, otaResDto, savedUser.getId()));

        log.info("Successfully created user account: '{}'.", savedUser);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.CREATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto activate(String token) {
        final OtaToken type = OtaToken.ACTIVATE_ACCOUNT;
        final OtaTokenEntity otaToken = otaTokenRepository
            .findByTokenAndTypeAndIsUsedFalseAndExpiredAtAfter(token, type, ZonedDateTime.now())
            .orElseThrow(() -> new OtaTokenException.OtaTokenNotFoundException(token, type));

        final UserEntity user = otaToken.getUser();
        if (user.getIsActivated()) {
            throw new UserException.UserAlreadyActivatedException(user.getUsername());
        }
        if (otaTokenService.checkIfIsExpired(otaToken.getExpiredAt())) {
            log.error("Attempt to activate account with expired token: '{}'.", otaToken);
            throw new OtaTokenException.OtaTokenNotFoundException(token, type);
        }
        otaToken.setUsed(true);
        user.setIsActivated(true);

        if (user.getMfaUser() != null) {
            user.getMfaUser().setMfaSecret(mfaProxyService.generateSecret());
            log.info("Successfully saved MFA secret key for user: '{}'.", user);
        }
        final UserEntity activatedUser = userRepository.save(user);

        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.INSTANTIATE_USER_RELATED_SETTINGS,
            activatedUser.getId());

        final DefaultUserProfileReqDto profileReqDto = DefaultUserProfileReqDto.builder()
            .initials(new char[]{ user.getFirstName().charAt(0), user.getLastName().charAt(0) })
            .userId(user.getId())
            .build();

        syncQueueHandler.sendNotNullWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto,
            ProfileImageDetailsResDto.class);

        final SendBaseEmailReqDto emailReqDto = accountMapper.mapToSendBaseEmailReq(activatedUser);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_NEW_ACCOUNT, emailReqDto);

        log.info("Successfully activated account for user: '{}'.", activatedUser);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    public BaseMessageResDto resend(ActivateAccountReqDto reqDto) {
        final UserEntity user = userRepository
            .findByLocalUsernameOrEmailAddress(reqDto.getEmailAddress())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.getEmailAddress()));

        final GenerateOtaResDto otaResDto = otaTokenService.generate(user, OtaToken.ACTIVATE_ACCOUNT);
        final SendTokenEmailReqDto emailReqDto = accountMapper
            .mapToSendTokenEmailReq(user, otaResDto, user.getId());

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_ACTIVATE_ACCOUNT, emailReqDto);

        log.info("Successfully resend activate account message for user: '{}'.", user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.RESEND_ACTIVATE_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto disable(boolean deleteMessages, PasswordReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = findUserAndCheckPasswordForLocal(reqDto, user);
        if (userEntity.getIsDisabled()) {
            throw new AccountException.AccountAlreadyDisabledException(user.getUsername());
        }
        refreshTokenRepository.deleteAllByUser_Id(user.getId());
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.REPLACE_PROFILE_IMAGE_WITH_LOCKED, user.getId());

        deleteUserMessages(deleteMessages, user);

        userEntity.setIsDisabled(true);
        cacheService.deleteCache(CacheName.USER_ENTITY_USER_ID, user.getId());

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_DISABLED_ACCOUNT,
            accountMapper.mapToSendBaseEmailReq(userEntity));

        log.info("Successfully disabled account for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.DISABLED_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto enable(HttpServletRequest req) {
        final String accessToken = jwtService.extractFromReq(req);
        final Claims claims = jwtService
            .extractClaims(accessToken)
            .orElseThrow(JwtException.JwtGeneralException::new);
        final String username = claims.getSubject();

        final UserEntity userEntity = userRepository
            .findByUsernameOrEmailAddress(username)
            .orElseThrow(() -> new UserException.UserNotExistException(username));
        if (!userEntity.getIsDisabled()) {
            throw new AccountException.AccountAlreadyEnabledException(username);
        }
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.REPLACE_LOCKED_WITH_PROFILE_IMAGE, userEntity.getId());
        cacheService.deleteCache(CacheName.USER_ENTITY_USER_ID, userEntity.getId());

        userEntity.setIsDisabled(false);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_ENABLED_ACCOUNT,
            accountMapper.mapToSendBaseEmailReq(userEntity));

        log.info("Successfully enabled account for user: '{}'.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.ENABLED_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto delete(boolean deleteMessages, PasswordReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = findUserAndCheckPasswordForLocal(reqDto, user);

        final boolean hasSomeGuilds = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.CHECK_USER_SPHERE_GUILDS, userEntity.getId(), Boolean.class);
        if (hasSomeGuilds) {
            throw new AccountException.UnableToDeleteAccountWithGuildsException(userEntity.getId());
        }
        if (userEntity.getExternalCredProvider()) {
            syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_OAUTH2_USER_DATA, userEntity.getId());
        }
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_USER_SETTINGS_DATA, userEntity.getId());
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_NOTIF_USER_SETTINGS, userEntity.getId());
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_USER_IMAGE_DATA, userEntity.getId());
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_USER_FROM_GUILDS, userEntity.getId());

        deleteUserMessages(deleteMessages, user);

        refreshTokenRepository.deleteAllByUser_Id(user.getId());
        userRepository.delete(userEntity);

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_DELETED_ACCOUNT,
            accountMapper.mapToSendBaseEmailReq(userEntity));

        log.info("Successfully deleted user account: '{}' with all related data.", userEntity);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.DELETED_ACCOUNT_RESPONSE_SUCCESS))
            .build();
    }

    private LocalDate parseToLocalDate(String birthDate) {
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private UserEntity findUserAndCheckPasswordForLocal(PasswordReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = userRepository
            .findById(user.getId())
            .orElseThrow(() -> new UserException.UserNotExistException(user.getUsername()));

        final CredentialsConfirmationReqDto confirmationReqDto = CredentialsConfirmationReqDto.builder()
            .userId(user.getId())
            .password(reqDto.getPassword())
            .mfaCode(reqDto.getMfaCode())
            .build();
        userService.checkUserCredentials(confirmationReqDto);
        return userEntity;
    }

    private void deleteUserMessages(boolean deleteMessages, AuthUserDetails user) {
        final UserTextChannelsResDto resDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_USER_TEXT_CHANNELS, user.getId(), UserTextChannelsResDto.class);
        if (deleteMessages) {
            syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_USER_MESSAGES,
                new DeleteUserMessagesReqDto(user.getId(), resDto.textChannelIds()));
        }
    }
}
