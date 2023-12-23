/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.check;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.visphere.auth.domain.user.UserEntity;
import pl.visphere.auth.domain.user.UserRepository;
import pl.visphere.auth.network.check.dto.CheckAlreadyExistResDto;
import pl.visphere.auth.network.check.dto.MyAccountReqDto;
import pl.visphere.auth.network.check.dto.MyAccountResDto;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.security.user.AuthUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SyncQueueHandler syncQueueHandler;

    @Override
    public CheckAlreadyExistResDto checkIfAccountPropAlreadyExist(AccountValueParam by, String value) {
        boolean alreadyExist = false;
        if (StringUtils.isNotEmpty(value)) {
            alreadyExist = switch (by) {
                case EMAIL -> userRepository.existsByEmailAddress(value);
                case USERNAME -> userRepository.existsByUsername(value);
            };
        }
        return createCheckParamDto(alreadyExist, by, value);
    }

    @Override
    public CheckAlreadyExistResDto checkIfLoggedAccountPropAlreadyExist(
        AccountValueParam by, String value, AuthUserDetails user
    ) {
        boolean alreadyExist = false;
        if (StringUtils.isNotEmpty(value)) {
            alreadyExist = switch (by) {
                case EMAIL -> userRepository.existsByEmailAddressAndIdIsNot(value, user.getId());
                case USERNAME -> userRepository.existsByUsernameAndIdIsNot(value, user.getId());
            };
        }
        return createCheckParamDto(alreadyExist, by, value);
    }

    @Override
    public List<MyAccountResDto> checkIfMyAccountsExists(List<MyAccountReqDto> reqDtos) {
        final List<MyAccountResDto> resDtos = new ArrayList<>();

        final List<String> usernames = reqDtos.stream()
            .map(MyAccountReqDto::getUsernameOrEmailAddress).toList();

        final List<UserEntity> foundUsers = userRepository
            .findAllByUsernameInOrEmailAddressIn(usernames, usernames);

        for (final MyAccountReqDto reqDto : reqDtos) {
            final MyAccountResDto resDto = modelMapper.map(reqDto, MyAccountResDto.class);
            String thumbnailUrl = StringUtils.EMPTY;
            if (reqDto.getVerified()) {
                final String username = reqDto.getUsernameOrEmailAddress();
                final Optional<UserEntity> foundUser = foundUsers.stream()
                    .filter(c -> c.getEmailAddress().equals(username) || c.getUsername().equals(username))
                    .findFirst();
                if (foundUser.isEmpty()) {
                    continue;
                }
                final ProfileImageDetailsReqDto imageDetailsReqDto = ProfileImageDetailsReqDto.builder()
                    .userId(foundUser.get().getId())
                    .isExternalCredentialsSupplier(foundUser.get().getExternalCredProvider())
                    .build();

                final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler
                    .sendNotNullWithBlockThread(QueueTopic.PROFILE_IMAGE_DETAILS, imageDetailsReqDto,
                        ProfileImageDetailsResDto.class);
                thumbnailUrl = profileImageDetails.getProfileImagePath();
            }
            resDto.setThumbnailUrl(thumbnailUrl);
            resDtos.add(resDto);
        }
        final int pos = resDtos.size();
        final int neg = reqDtos.size() - pos;

        log.info("Successfully checked accounts with pos: '{}' and neg: '{}'. Summary: '{}'.", pos, neg, resDtos);
        return resDtos;
    }

    private CheckAlreadyExistResDto createCheckParamDto(boolean alreadyExist, AccountValueParam by, String value) {
        final CheckAlreadyExistResDto resDto = CheckAlreadyExistResDto.builder()
            .alreadyExist(alreadyExist)
            .build();
        log.info("Successfully check account prop: '{}' with value: '{}' and response: '{}'.", by, value, resDto);
        return resDto;
    }
}
