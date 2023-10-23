/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.visphere.auth.domain.blacklistjwt.BlackListJwtRepository;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.auth.domain.role.RoleRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.RoleException;
import pl.visphere.auth.exception.UserException;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.SyncListenerHandler;
import pl.visphere.lib.kafka.payload.PersistUserReqDto;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class AuthKafkaListener {
    private final SyncListenerHandler syncListenerHandler;
    private final BlackListJwtRepository blackListJwtRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final AuthKafkaMapper authKafkaMapper;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @KafkaListener(topics = "${visphere.kafka.topic.persist-user}")
    void persistUserListener(Message<PersistUserReqDto> userDetails) {
        syncListenerHandler.parseAndSendResponse(userDetails, details -> {
            if (userRepository.existsByUsernameOrEmailAddress(details.getUsername(), details.getEmailAddress())) {
                throw new UserException.UserAlreadyExistException(details.getUsername(), details.getEmailAddress());
            }
            final RoleEntity role = roleRepository
                .findByRole(AppGrantedAuthority.USER)
                .orElseThrow(() -> new RoleException.RoleNotExistException(AppGrantedAuthority.USER));

            final UserEntity user = modelMapper.map(details, UserEntity.class);
            if (details.getSecondEmailAddress().equals(StringUtils.EMPTY)) {
                user.setSecondEmailAddress(null);
            }
            final TokenData generateRefreshToken = jwtService.generateRefreshToken();
            final RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .refreshToken(generateRefreshToken.token())
                .expiringAt(jwtService.convertToZonedDateTime(generateRefreshToken.expiredAt()))
                .build();

            user.setPassword(passwordEncoder.encode(details.getPassword()));
            user.addRole(role);
            user.persistRefreshToken(refreshToken);

            final UserEntity savedUser = userRepository.save(user);
            final Long id = savedUser.getId();

            log.info("User entity with id: '{}' was successfully persisted: '{}'", id, user);
            return id;
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.check-user}")
    void checkUserListener(Message<String> username) {
        syncListenerHandler.parseAndSendResponse(username, identity -> {
            final UserEntity user = userRepository
                .findByUsernameOrEmailAddress(identity)
                .orElseThrow(() -> new UserException.UserNotExistException(identity));

            final Set<AppGrantedAuthority> roles = user.getRoles()
                .stream().map(RoleEntity::getRole)
                .collect(Collectors.toSet());

            return authKafkaMapper.mapToUserDetailsResDto(user, roles);
        });
    }

    @KafkaListener(topics = "${visphere.kafka.topic.jwt-is-on-blacklist}")
    void jwtIsOnBlaclistListener(Message<String> token) {
        syncListenerHandler.parseAndSendResponse(token, blackListJwtRepository::existsByExpiredJwt);
    }
}
