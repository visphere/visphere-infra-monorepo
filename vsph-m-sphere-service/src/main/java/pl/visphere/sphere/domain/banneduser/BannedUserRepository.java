/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.banneduser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUserEntity, Long> {
    List<BannedUserEntity> findAllByGuild_IdAndGuild_OwnerId(Long guildId, Long ownerId);
    Optional<BannedUserEntity> findByUserIdAndGuild_Id(Long userId, Long guildId);
    boolean existsByGuild_IdAndUserId(Long guildId, Long userId);
}
