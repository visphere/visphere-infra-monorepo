/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.role.RoleEntity;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.exception.UserException;
import pl.visphere.lib.kafka.payload.auth.CheckUserResDto;
import pl.visphere.lib.kafka.payload.auth.UserDetailsResDto;
import pl.visphere.lib.security.user.AppGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Override
    public CheckUserResDto checkUser(String usernameOrEmailAddress) {
        final UserEntity user = userRepository
            .findByLocalUsernameOrEmailAddress(usernameOrEmailAddress)
            .orElseThrow(() -> new UserException.UserNotExistException(usernameOrEmailAddress));

        final Set<AppGrantedAuthority> roles = user.getRoles()
            .stream().map(RoleEntity::getRole)
            .collect(Collectors.toSet());

        final CheckUserResDto resDto = modelMapper.map(user, CheckUserResDto.class);
        resDto.setAuthorities(roles);

        log.info("Successfully find user with details: '{}'", resDto);
        return resDto;
    }

    @Override
    public UserDetailsResDto getUserDetails(Long userId) {
        final UserEntity user = userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException.UserNotExistException(userId));

        final UserDetailsResDto resDto = modelMapper.map(user, UserDetailsResDto.class);

        log.info("Successfully find user and map to details: '{}'", resDto);
        return resDto;
    }
}
