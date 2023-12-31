/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.notification.PersistUserNotifSettingsReqDto;
import pl.visphere.lib.kafka.payload.user.*;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AppGrantedAuthority;
import pl.visphere.user.domain.mfauser.MfaUserEntity;
import pl.visphere.user.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.user.domain.refreshtoken.RefreshTokenRepository;
import pl.visphere.user.domain.role.RoleEntity;
import pl.visphere.user.domain.role.RoleRepository;
import pl.visphere.user.domain.user.UserEntity;
import pl.visphere.user.domain.user.UserRepository;
import pl.visphere.user.exception.AccountException;
import pl.visphere.user.exception.RoleException;
import pl.visphere.user.service.mfa.MfaProxyService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final SyncQueueHandler syncQueueHandler;
    private final PasswordEncoder passwordEncoder;
    private final MfaProxyService mfaProxyService;
    private final I18nService i18nService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public CheckUserResDto checkUser(String usernameOrEmailAddress) {
        final UserEntity user = userRepository
            .findByUsernameOrEmailAddress(usernameOrEmailAddress)
            .orElseThrow(() -> new UserException.UserNotExistException(usernameOrEmailAddress));

        final Set<AppGrantedAuthority> authorities = user.getRoles()
            .stream().map(RoleEntity::getRole)
            .collect(Collectors.toSet());

        final CheckUserResDto resDto = modelMapper.map(user, CheckUserResDto.class);
        resDto.setAuthorities(authorities);

        log.info("Successfully find user with details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public UserDetailsResDto getUserDetails(Long userId) {
        final UserEntity user = userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException.UserNotExistException(userId));

        final UserDetailsResDto resDto = modelMapper.map(user, UserDetailsResDto.class);
        resDto.setActivated(user.getIsActivated());
        resDto.setJoinDate(createJoinDate(user));
        resDto.setLocked(user.getIsDisabled());
        resDto.setExternalCredentialsSupplier(user.getExternalCredProvider());

        log.info("Successfully find user and map to details: '{}'.", resDto);
        return resDto;
    }

    @Override
    public PersistOAuth2UserResDto persistNewUser(PersistOAuth2UserReqDto reqDto) {
        if (userRepository.existsByEmailAddress(reqDto.getEmailAddress())) {
            throw new UserException.UserAlreadyExistException(reqDto.getEmailAddress());
        }
        String temporaryUsername;
        do {
            temporaryUsername = new StringJoiner(StringUtils.EMPTY)
                .add(reqDto.getFirstName())
                .add(reqDto.getLastName())
                .add(RandomStringUtils.randomNumeric(4))
                .toString()
                .toLowerCase();
            temporaryUsername = StringUtils.stripAccents(temporaryUsername);
        } while (userRepository.existsByUsername(temporaryUsername));

        final RoleEntity role = roleRepository
            .findByRole(AppGrantedAuthority.USER)
            .orElseThrow(() -> new RoleException.RoleNotExistException(AppGrantedAuthority.USER));

        final UserEntity user = modelMapper.map(reqDto, UserEntity.class);
        user.setUsername(temporaryUsername);
        user.setExternalCredProvider(true);
        user.addRole(role);

        final UserEntity savedUser = userRepository.save(user);
        final Set<AppGrantedAuthority> authorities = user.getRoles()
            .stream().map(RoleEntity::getRole)
            .collect(Collectors.toSet());

        log.info("Successfully persist new user with external credentials provider: '{}'.", savedUser);
        return PersistOAuth2UserResDto.builder()
            .userId(savedUser.getId())
            .username(temporaryUsername)
            .authorities(authorities)
            .build();
    }

    @Override
    public OAuth2UserDetailsResDto getOAuth2UserDetails(Long userId) {
        final UserEntity user = userRepository
            .findByIdFetchRoles(userId)
            .orElseThrow(() -> new UserException.UserNotExistException(userId));

        final Set<AppGrantedAuthority> authorities = user.getRoles()
            .stream().map(RoleEntity::getRole)
            .collect(Collectors.toSet());

        log.info("Successfully get OAuth2 user details for external credentials provider: '{}'.", user);
        return OAuth2UserDetailsResDto.builder()
            .username(user.getUsername())
            .emailAddress(user.getEmailAddress())
            .authorities(authorities)
            .isActivated(user.getIsActivated())
            .build();
    }

    @Override
    @Transactional
    public LoginOAuth2UserDetailsResDto updateOAuth2UserDetails(UpdateOAuth2UserDetailsReqDto reqDto) {
        final UserEntity user = userRepository
            .findByIdAndExternalCredProviderIsTrue(reqDto.getUserId())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.getUserId()));

        if (user.getIsActivated()) {
            throw new UserException.UserAlreadyActivatedException(user.getUsername());
        }
        if (userRepository.existsByUsernameAndIdIsNot(reqDto.getUsername(), reqDto.getUserId())) {
            throw new UserException.UserAlreadyExistException(reqDto.getUsername());
        }
        final String firstName = StringUtils.capitalize(reqDto.getFirstName());
        final String lastName = StringUtils.capitalize(reqDto.getLastName());

        user.setUsername(reqDto.getUsername());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthDate(LocalDate.parse(reqDto.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setIsActivated(true);

        final PersistUserNotifSettingsReqDto notifSettingsReqDto = PersistUserNotifSettingsReqDto.builder()
            .userId(user.getId())
            .isEmailNotifsEnabled(reqDto.getAllowNotifs())
            .build();
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.PERSIST_NOTIF_USER_SETTINGS, notifSettingsReqDto);

        final LoginOAuth2UserDetailsResDto resDto = generateTokens(user, firstName, lastName);

        log.info("Successfully updated OAuth2 user: '{}' details for external credentials provider.", user);
        return resDto;
    }

    @Override
    @Transactional
    public LoginOAuth2UserDetailsResDto loginOAuth2User(Long userId) {
        final UserEntity user = userRepository
            .findByIdAndExternalCredProviderIsTrue(userId)
            .orElseThrow(() -> new UserException.UserNotExistException(userId));

        if (!user.getIsActivated()) {
            throw new UserException.UserNotExistOrNotActivatedException(userId);
        }
        final LoginOAuth2UserDetailsResDto resDto = generateTokens(user);

        log.info("Successfully log in OAuth2 user: '{}' details for external credentials provider.", user);
        return resDto;
    }

    @Override
    public void checkUserCredentials(CredentialsConfirmationReqDto reqDto) {
        final UserEntity userEntity = userRepository
            .findById(reqDto.userId())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.userId()));

        final MfaUserEntity mfaUser = userEntity.getMfaUser();
        final boolean checkedPassword = passwordEncoder.matches(reqDto.password(), userEntity.getPassword());
        final String mfaToken = reqDto.mfaCode();

        if ((mfaUser == null || !mfaUser.getMfaIsSetup())) {
            if (!checkedPassword && !userEntity.getExternalCredProvider()) {
                throw new AccountException.IncorrectPasswordException(userEntity.getUsername());
            }
        } else if (mfaProxyService.isOtpNotValid(mfaUser.getMfaSecret(), mfaToken) || !checkedPassword) {
            throw new AccountException.IncorrectPasswordOrMfaCodeException(userEntity.getUsername(), mfaToken);
        }
    }

    @Override
    public CheckUserSessionResDto checkUserSession(CheckUserSessionReqDto reqDto) {
        return new CheckUserSessionResDto(refreshTokenRepository.findByRefreshToken(reqDto.token())
            .map(refreshTokenEntity -> refreshTokenEntity.getUser().getId())
            .orElse(null));
    }

    @Override
    public UsersDetailsResDto getUsersDetails(UsersDetailsReqDto reqDto) {
        final List<UserEntity> users = userRepository.findAllByIdIn(reqDto.userIds());
        final Map<Long, UserDetails> userDetails = new HashMap<>(reqDto.userIds().size());

        final List<Long> foundedUsersIds = users.stream().map(UserEntity::getId).toList();
        final List<Long> missingUsersIds = reqDto.userIds().stream()
            .filter(userId -> !foundedUsersIds.contains(userId))
            .toList();

        for (final UserEntity user : users) {
            String fullName = i18nService.getMessage(LibLocaleSet.ACCOUNT_LOCKED_PLACEHOLDER);
            if (!user.getIsDisabled()) {
                fullName = user.getFirstName() + StringUtils.SPACE + user.getLastName();
            }
            final UserDetails details = UserDetails.builder()
                .fullName(fullName)
                .externalSupplier(user.getExternalCredProvider())
                .accountDeleted(false)
                .build();
            userDetails.put(user.getId(), details);
        }
        for (final Long userId : missingUsersIds) {
            final UserDetails details = UserDetails.builder()
                .fullName(i18nService.getMessage(LibLocaleSet.ACCOUNT_DELETED_PLACEHOLDER))
                .externalSupplier(false)
                .accountDeleted(true)
                .build();
            userDetails.put(userId, details);
        }
        log.info("Successfully processed and parsed: '{}' (missing: '{}') results: '{}'",
            userDetails.size(), missingUsersIds.size(), userDetails);
        return new UsersDetailsResDto(userDetails);
    }

    private LoginOAuth2UserDetailsResDto generateTokens(UserEntity user, String firstName, String lastName) {
        final TokenData access = jwtService
            .generateAccessToken(user.getId(), user.getUsername(), user.getEmailAddress());

        String refrehToken = StringUtils.EMPTY;
        if (!user.getIsDisabled()) {
            final TokenData refresh = jwtService.generateRefreshToken();
            final RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .refreshToken(refresh.token())
                .expiringAt(jwtService.convertToZonedDateTime(refresh.expiredAt()))
                .build();
            user.persistRefreshToken(refreshTokenEntity);
            refrehToken = refresh.token();
        }
        log.info("Successfully persisted refresh token for user: '{}'.", user);
        return LoginOAuth2UserDetailsResDto.builder()
            .username(user.getUsername())
            .fullName(firstName + StringUtils.SPACE + lastName)
            .emailAddress(user.getEmailAddress())
            .accessToken(access.token())
            .refreshToken(refrehToken)
            .isDisabled(user.getIsDisabled())
            .joinDate(createJoinDate(user))
            .build();
    }

    private LoginOAuth2UserDetailsResDto generateTokens(UserEntity user) {
        return generateTokens(user, user.getFirstName(), user.getLastName());
    }

    private LocalDate createJoinDate(UserEntity user) {
        return user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
