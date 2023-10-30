/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.refreshtoken.RefreshTokenEntity;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.auth.domain.role.RoleRepository;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.RoleException;
import pl.visphere.auth.exception.UserException;
import pl.visphere.auth.service.otatoken.OtaTokenService;
import pl.visphere.auth.service.otatoken.dto.GenerateOtaResDto;
import pl.visphere.lib.jwt.JwtService;
import pl.visphere.lib.jwt.TokenData;
import pl.visphere.lib.kafka.payload.auth.*;
import pl.visphere.lib.security.OtaToken;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final OtaTokenService otaTokenService;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public CreateUserResDto createUser(CreateUserReqDto reqDto) {
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

        final TokenData generateRefreshToken = jwtService.generateRefreshToken();
        final RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
            .refreshToken(generateRefreshToken.token())
            .expiringAt(jwtService.convertToZonedDateTime(generateRefreshToken.expiredAt()))
            .build();

        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
        user.addRole(role);
        user.persistRefreshToken(refreshToken);

        final UserEntity savedUser = userRepository.save(user);
        final GenerateOtaResDto otaResDto = otaTokenService.generate(savedUser, OtaToken.ACTIVATE_ACCOUNT);

        log.info("Successfully created user: '{}'", savedUser);
        return CreateUserResDto.builder()
            .userId(savedUser.getId())
            .token(otaResDto.token())
            .expiredAt(otaResDto.expiredAt())
            .build();
    }

    @Override
    public ActivateUserResDto activateUser(ActivateUserReqDto reqDto) {
        final UserEntity user = userRepository
            .findByUsernameOrEmailAddress(reqDto.emailAddress())
            .orElseThrow(() -> new UserException.UserNotExistException(reqDto.emailAddress()));

        if (user.getActivated()) {
            throw new UserException.UserAlreadyActivatedException(user);
        }
        otaTokenService.validate(user.getId(), reqDto.token(), OtaToken.ACTIVATE_ACCOUNT);

        user.setActivated(true);
        final UserEntity activatedUser = userRepository.save(user);

        log.info("Successfully activated user: '{}'", activatedUser);
        return ActivateUserResDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .isMfaEnabled(user.getEnabledMfa())
            .build();
    }

    @Override
    public CheckUserResDto checkUser(String usernameOrEmailAddress) {
        final UserEntity user = userRepository
            .findByUsernameOrEmailAddress(usernameOrEmailAddress)
            .orElseThrow(() -> new UserException.UserNotExistException(usernameOrEmailAddress));

        final Set<AppGrantedAuthority> roles = user.getRoles()
            .stream().map(RoleEntity::getRole)
            .collect(Collectors.toSet());

        final CheckUserResDto resDto = modelMapper.map(user, CheckUserResDto.class);
        resDto.setAuthorities(roles);

        log.info("Successfully find user with details: '{}'", resDto);
        return resDto;
    }
}
