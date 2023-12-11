/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.auth.cache.CacheName;
import pl.visphere.auth.domain.mfauser.MfaUserEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenEntity;
import pl.visphere.auth.domain.otatoken.OtaTokenRepository;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.auth.domain.role.RoleRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.AccountException;
import pl.visphere.auth.exception.OtaTokenException;
import pl.visphere.auth.exception.RoleException;
import pl.visphere.auth.i18n.LocaleSet;
import pl.visphere.auth.network.account.dto.*;
import pl.visphere.auth.service.mfa.MfaProxyService;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.cache.CacheService;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.async.AsyncQueueHandler;
import pl.visphere.lib.kafka.payload.multimedia.DefaultUserProfileReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.notification.SendBaseEmailReqDto;
import pl.visphere.lib.kafka.payload.notification.SendTokenEmailReqDto;
import pl.visphere.lib.kafka.payload.oauth2.OAuth2DetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AppGrantedAuthority;
import pl.visphere.lib.security.user.AuthUserDetails;

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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtaTokenRepository otaTokenRepository;

    @Override
    public AccountDetailsResDto getAccountDetails(AuthUserDetails user) {
        final UserEntity userEntity = getUserProxyFromCache(user);

        final boolean isExternalCredSupplier = userEntity.getExternalCredProvider();

        String credentialsSupplier = "local";
        if (isExternalCredSupplier) {
            final OAuth2DetailsResDto detailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.GET_OAUTH2_DETAILS, user.getId(), OAuth2DetailsResDto.class);
            credentialsSupplier = detailsResDto.supplier();
        }
        final AccountDetailsResDto resDto = accountMapper.mapToAccountDetailsRes(userEntity, credentialsSupplier);
        log.info("Successfully find and fetched user account details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public UpdateAccountDetailsResDto updateAccountDetails(UpdateAccountDetailsReqDto reqDto, AuthUserDetails user) {
        final UserEntity userEntity = getUserProxyFromCache(user);
        final String username = reqDto.getUsername();

        if (userRepository.existsByUsernameAndIdIsNot(username, user.getId())) {
            throw new UserException.UserAlreadyExistException(username);
        }
        userEntity.setUsername(reqDto.getUsername());
        userEntity.setFirstName(StringUtils.capitalize(reqDto.getFirstName()));
        userEntity.setLastName(StringUtils.capitalize(reqDto.getLastName()));
        userEntity.setBirthDate(parseToLocalDate(reqDto.getBirthDate()));

        final TokenData tokenData = jwtService
            .generateAccessToken(user.getId(), reqDto.getUsername(), user.getEmailAddress());

        final UserEntity saved = userRepository.save(userEntity);
        cacheService.updateCache(CacheName.USER_ENTITY_USER_ID, userEntity.getId(), saved);

        log.info("Successfully updated user account details for user: '{}'.", userEntity);
        return UpdateAccountDetailsResDto.builder()
            .message(i18nService.getMessage(LocaleSet.UDPATE_ACCOUNT_DETAILS_RESPONSE_SUCCESS))
            .accessToken(tokenData.token())
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

        // TODO: save global notification settings in notifications microservice table

        if (reqDto.getEnabledMfa()) {
            user.persistMfaUser(new MfaUserEntity());
            log.info("Successfully addded MFA user details user: '{}'.", user);
        }
        final UserEntity savedUser = userRepository.save(user);
        final GenerateOtaResDto otaResDto = otaTokenService.generate(savedUser, OtaToken.ACTIVATE_ACCOUNT);

        final SendTokenEmailReqDto emailReqDto = accountMapper
            .mapToSendTokenEmailReq(reqDto, otaResDto, savedUser.getId());

        asyncQueueHandler.sendAsyncWithNonBlockingThread(QueueTopic.EMAIL_ACTIVATE_ACCOUNT, emailReqDto);

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

        final ProfileImageDetailsResDto profileResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GENERATE_DEFAULT_USER_PROFILE, profileReqDto,
                ProfileImageDetailsResDto.class);

        final SendBaseEmailReqDto emailReqDto = accountMapper.mapToSendBaseEmailReq(activatedUser, profileResDto);
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
    private UserEntity getUserProxyFromCache(AuthUserDetails user) {
        return cacheService
            .getSafetyFromCache(CacheName.USER_ENTITY_USER_ID, user.getId(), UserEntity.class,
                () -> userRepository.findById(user.getId()))
            .orElseThrow(() -> new UserException.UserNotExistException(user.getId()));
    }

    private LocalDate parseToLocalDate(String birthDate) {
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void sendEmail(UserEntity user, QueueTopic kafkaTopic) {
        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, user.getId(),
                ProfileImageDetailsResDto.class);

        final SendBaseEmailReqDto emailReqDto = accountMapper.mapToSendBaseEmailReq(user, profileImageDetails);
        asyncQueueHandler.sendAsyncWithNonBlockingThread(kafkaTopic, emailReqDto);
    }
}
