/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.network.participant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.LibLocaleSet;
import pl.visphere.lib.exception.app.UserException;
import pl.visphere.lib.i18n.I18nService;
import pl.visphere.lib.kafka.QueueTopic;
import pl.visphere.lib.kafka.payload.chat.DeleteUserMessagesReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsReqDto;
import pl.visphere.lib.kafka.payload.multimedia.ProfileImageDetailsResDto;
import pl.visphere.lib.kafka.payload.user.CredentialsConfirmationReqDto;
import pl.visphere.lib.kafka.payload.user.UserDetailsResDto;
import pl.visphere.lib.kafka.sync.SyncQueueHandler;
import pl.visphere.lib.s3.S3Bucket;
import pl.visphere.lib.s3.S3Client;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.sphere.domain.banneduser.BannedUserEntity;
import pl.visphere.sphere.domain.banneduser.BannedUserRepository;
import pl.visphere.sphere.domain.guild.GuildEntity;
import pl.visphere.sphere.domain.guild.GuildRepository;
import pl.visphere.sphere.domain.textchannel.TextChannelEntity;
import pl.visphere.sphere.domain.textchannel.TextChannelRepository;
import pl.visphere.sphere.domain.userguild.UserGuildEntity;
import pl.visphere.sphere.domain.userguild.UserGuildRepository;
import pl.visphere.sphere.exception.BannedUserException;
import pl.visphere.sphere.exception.SphereGuildException;
import pl.visphere.sphere.exception.UserGuildException;
import pl.visphere.sphere.i18n.LocaleSet;
import pl.visphere.sphere.network.guild.dto.PasswordReqDto;
import pl.visphere.sphere.network.participant.dto.BannerMemberDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipant;
import pl.visphere.sphere.network.participant.dto.GuildParticipantDetailsResDto;
import pl.visphere.sphere.network.participant.dto.GuildParticipantsResDto;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class ParticipantServiceImpl implements ParticipantService {
    private final SyncQueueHandler syncQueueHandler;
    private final I18nService i18nService;
    private final S3Client s3Client;

    private final GuildRepository guildRepository;
    private final UserGuildRepository userGuildRepository;
    private final BannedUserRepository bannedUserRepository;
    private final TextChannelRepository textChannelRepository;

    @Override
    public GuildParticipantsResDto getAllGuildParticipants(long guildId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findById(guildId)
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        final List<UserGuildEntity> usersGuild = userGuildRepository.findAllByGuild_Id(guildId);
        final List<GuildParticipant> guildMembers = new ArrayList<>(usersGuild.size());

        for (final UserGuildEntity userGuild : usersGuild) {
            final UserDetailsResDto userDetailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, userGuild.getUserId(), UserDetailsResDto.class);

            final ProfileImageDetailsReqDto profileImageDetailsReqDto = ProfileImageDetailsReqDto.builder()
                .userId(userGuild.getUserId())
                .isExternalCredentialsSupplier(userDetailsResDto.isExternalCredentialsSupplier())
                .build();

            final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler.sendNotNullWithBlockThread(
                QueueTopic.PROFILE_IMAGE_DETAILS, profileImageDetailsReqDto, ProfileImageDetailsResDto.class);

            String fullName = createFullName(userDetailsResDto);
            if (userDetailsResDto.isLocked()) {
                fullName = i18nService.getMessage(LibLocaleSet.ACCOUNT_LOCKED_PLACEHOLDER);
            }
            final GuildParticipant member = GuildParticipant.builder()
                .id(userGuild.getUserId())
                .fullName(fullName)
                .profileImageUrl(profileImageDetails.getProfileImagePath())
                .build();

            guildMembers.add(member);
        }
        final GuildParticipant ownerParticipant = guildMembers.stream()
            .filter(participant -> participant.id().equals(guild.getOwnerId()))
            .findFirst()
            .orElseThrow(() -> new SphereGuildException.SphereGuildHasNoOwnerException(guildId));

        final List<GuildParticipant> memberParticipants = guildMembers.stream()
            .filter(participant -> !participant.id().equals(guild.getOwnerId()))
            .sorted((p1, p2) -> p1.fullName().compareToIgnoreCase(p2.fullName()))
            .toList();

        log.info("Successfully found: '{}' participants with: '{}' members in guild with ID: '{}'.",
            guildMembers.size(), memberParticipants.size(), guildId);

        return GuildParticipantsResDto.builder()
            .guildId(guild.getId())
            .owner(ownerParticipant)
            .members(memberParticipants)
            .build();
    }

    @Override
    public GuildParticipantDetailsResDto getGuildParticipantDetails(long guildId, long userId, AuthUserDetails user) {
        final GuildEntity guild = guildRepository
            .findById(guildId)
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));

        if (!userGuildRepository.existsByUserIdAndGuild_Id(userId, guildId)) {
            throw new UserException.UserNotExistException(userId);
        }
        final UserGuildEntity userGuild = userGuildRepository
            .findByUserIdAndGuild_Id(userId, guildId)
            .orElseThrow(() -> new UserGuildException.UserIsNotGuildParticipantException(userId, guildId));

        final UserDetailsResDto userDetailsResDto = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, userId, UserDetailsResDto.class);

        final ProfileImageDetailsReqDto profileImageDetailsReqDto = ProfileImageDetailsReqDto.builder()
            .userId(userGuild.getUserId())
            .isExternalCredentialsSupplier(userDetailsResDto.isExternalCredentialsSupplier())
            .build();

        final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler.sendNotNullWithBlockThread(
            QueueTopic.PROFILE_IMAGE_DETAILS, profileImageDetailsReqDto, ProfileImageDetailsResDto.class);

        final ProfileImageDetailsResDto guildProfileImageDetails = syncQueueHandler
            .sendNotNullWithBlockThread(QueueTopic.GET_GUILD_PROFILE_IMAGE_DETAILS, guildId,
                ProfileImageDetailsResDto.class);

        String fullName = createFullName(userDetailsResDto);
        String username = userDetailsResDto.getUsername();
        if (userDetailsResDto.isLocked()) {
            fullName = i18nService.getMessage(LibLocaleSet.ACCOUNT_LOCKED_PLACEHOLDER);
            username = "-";
        }
        final GuildParticipantDetailsResDto resDto = GuildParticipantDetailsResDto.builder()
            .id(userId)
            .fullName(fullName)
            .username(username)
            .joinDate(userDetailsResDto.getJoinDate())
            .memberSinceDate(userGuild.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
            .profileColor(profileImageDetails.getProfileColor())
            .profileImageUrl(profileImageDetails.getProfileImagePath())
            .isOwner(Objects.equals(userId, guild.getOwnerId()))
            .guildProfileImageUrl(guildProfileImageDetails.getProfileImagePath())
            .isLoggedUser(Objects.equals(userId, user.getId()))
            .build();

        log.info("Successfully found: '{}' participant details for guild with ID: '{}'.", resDto, guildId);
        return resDto;
    }

    @Override
    public List<BannerMemberDetailsResDto> getAllBannedParticipants(long guildId, AuthUserDetails user) {
        final List<BannedUserEntity> bannedUsers = bannedUserRepository
            .findAllByGuild_IdAndGuild_OwnerId(guildId, user.getId());

        final List<BannerMemberDetailsResDto> bannedGuildMembers = new ArrayList<>(bannedUsers.size());
        for (final BannedUserEntity bannedUser : bannedUsers) {
            final UserDetailsResDto userDetailsResDto = syncQueueHandler
                .sendNotNullWithBlockThread(QueueTopic.USER_DETAILS, bannedUser.getUserId(), UserDetailsResDto.class);

            final ProfileImageDetailsReqDto profileImageDetailsReqDto = ProfileImageDetailsReqDto.builder()
                .userId(bannedUser.getUserId())
                .isExternalCredentialsSupplier(userDetailsResDto.isExternalCredentialsSupplier())
                .build();

            final ProfileImageDetailsResDto profileImageDetails = syncQueueHandler.sendNotNullWithBlockThread(
                QueueTopic.PROFILE_IMAGE_DETAILS, profileImageDetailsReqDto, ProfileImageDetailsResDto.class);

            String fullName = createFullName(userDetailsResDto);
            String username = userDetailsResDto.getUsername();
            if (userDetailsResDto.isLocked()) {
                fullName = i18nService.getMessage(LibLocaleSet.ACCOUNT_LOCKED_PLACEHOLDER);
                username = "-";
            }
            final BannerMemberDetailsResDto resDto = BannerMemberDetailsResDto.builder()
                .id(bannedUser.getUserId())
                .fullName(fullName)
                .username(username)
                .profileColor(profileImageDetails.getProfileColor())
                .profileImageUrl(profileImageDetails.getProfileImagePath())
                .build();

            bannedGuildMembers.add(resDto);
        }
        log.info("Successfully found: '{}' banned members in guild with ID: '{}'.", bannedGuildMembers.size(), guildId);
        return bannedGuildMembers;
    }

    @Override
    @Transactional
    public BaseMessageResDto leaveGuild(long guildId, boolean deleteAllMessages, AuthUserDetails user) {
        final UserGuildEntity userGuild = userGuildRepository
            .findByUserIdAndGuild_Id(user.getId(), guildId)
            .orElseThrow(() -> new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId));

        final GuildEntity guild = userGuild.getGuild();
        if (guild.getOwnerId().equals(user.getId())) {
            throw new UserGuildException.DeleteGuildOwnerException(guildId, user.getId());
        }
        userGuild.setGuild(null);
        userGuildRepository.delete(userGuild);

        findTextChannelsAndDeleteUserMessages(guild, user.getId(), deleteAllMessages);

        log.info("Successfully leave guild with ID: '{}'", guildId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_LEAVE_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto kickFromGuild(long guildId, long userId, boolean deleteAllMessages, AuthUserDetails user) {
        final GuildEntity guild = findGuildByOwnerId(guildId, user);

        final UserGuildEntity userGuild = userGuildRepository
            .findByUserIdAndGuild_Id(userId, guild.getId())
            .orElseThrow(() -> new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId));

        if (userGuild.getGuild().getOwnerId().equals(userId)) {
            throw new UserGuildException.DeleteGuildOwnerException(guildId, user.getId());
        }
        userGuild.setGuild(null);
        userGuildRepository.delete(userGuild);

        findTextChannelsAndDeleteUserMessages(guild, userId, deleteAllMessages);

        log.info("Successfully kick member with ID: '{}' form guild with ID: '{}'", userId, guildId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_KICK_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto unbanFromGuild(long guildId, long userId, AuthUserDetails user) {
        final GuildEntity guild = findGuildByOwnerId(guildId, user);
        final BannedUserEntity bannedUser = bannedUserRepository
            .findByUserIdAndGuild_Id(userId, guild.getId())
            .orElseThrow(() -> new BannedUserException.UserIsNotBannedException(user.getId(), guildId));

        bannedUser.setGuild(null);
        bannedUserRepository.delete(bannedUser);

        log.info("Successfully unban member with ID: '{}' form guild with ID: '{}'", userId, guildId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_UNBAN_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto banFromGuild(long guildId, long userId, boolean deleteAllMessages, AuthUserDetails user) {
        final GuildEntity guild = findGuildByOwnerId(guildId, user);
        final UserGuildEntity bannedUserGuild = userGuildRepository
            .findByUserIdAndGuild_Id(userId, guild.getId())
            .orElseThrow(() -> new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId));

        bannedUserGuild.setGuild(null);
        userGuildRepository.delete(bannedUserGuild);

        final BannedUserEntity bannedUser = BannedUserEntity.builder()
            .userId(userId)
            .build();
        guild.persistBannedUser(bannedUser);

        findTextChannelsAndDeleteUserMessages(guild, userId, deleteAllMessages);

        log.info("Successfully ban member with ID: '{}' form guild with ID: '{}'", userId, guildId);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_BAN_RESPONSE_SUCCESS))
            .build();
    }

    @Override
    @Transactional
    public BaseMessageResDto delegateGuildProprietyToUser(
        long guildId, long userId, PasswordReqDto reqDto, AuthUserDetails user
    ) {
        final CredentialsConfirmationReqDto confirmationReqDto = CredentialsConfirmationReqDto.builder()
            .userId(user.getId())
            .password(reqDto.getPassword())
            .mfaCode(reqDto.getMfaCode())
            .build();
        syncQueueHandler.sendNullableWithBlockThread(QueueTopic.CHECK_USER_CREDENTIALS, confirmationReqDto);

        final GuildEntity guild = findGuildByOwnerId(guildId, user);
        if (!userGuildRepository.existsByUserIdAndGuild_Id(userId, guild.getId())) {
            throw new UserGuildException.UserIsNotGuildParticipantException(user.getId(), guildId);
        }
        guild.setOwnerId(userId);

        log.info("Successfully delegated propriety for guild wit ID: '{}' from user with ID: '{}' to ID: '{}'.",
            guildId, userId, user);
        return BaseMessageResDto.builder()
            .message(i18nService.getMessage(LocaleSet.SPHERE_GUILD_DELEGATE_PROPRIETY_TO_USER_RESPONSE_SUCCESS))
            .build();
    }

    private String createFullName(UserDetailsResDto detailsResDto) {
        return detailsResDto.getFirstName() + StringUtils.SPACE + detailsResDto.getLastName();
    }

    private GuildEntity findGuildByOwnerId(long guildId, AuthUserDetails user) {
        return guildRepository
            .findByIdAndOwnerId(guildId, user.getId())
            .orElseThrow(() -> new SphereGuildException.SphereGuildNotFoundException(guildId));
    }

    private void findTextChannelsAndDeleteUserMessages(GuildEntity guild, Long userId, boolean deleteAllMessages) {
        final List<Long> textChannelIds = textChannelRepository
            .findAllByGuild_Id(guild.getId())
            .stream().map(TextChannelEntity::getId)
            .toList();
        if (deleteAllMessages) {
            syncQueueHandler.sendNullableWithBlockThread(QueueTopic.DELETE_USER_MESSAGES,
                new DeleteUserMessagesReqDto(userId, textChannelIds));
            for (final Long textChannelId : textChannelIds) {
                s3Client.clearObjects(S3Bucket.ATTACHMENTS, String.format("%s/%s/%s",
                    guild.getId(), textChannelId, userId));
            }
        }
    }
}
