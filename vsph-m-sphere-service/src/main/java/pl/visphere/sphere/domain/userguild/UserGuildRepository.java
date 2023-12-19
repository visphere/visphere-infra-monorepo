/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.userguild;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGuildRepository extends JpaRepository<UserGuildEntity, Long> {
    List<UserGuildEntity> findAllByUserIdAndBannedIsFalse(Long userId);
    List<UserGuildEntity> findAllByGuild_IdAndBannedIsFalse(Long guildId);
    List<UserGuildEntity> findAllByGuild_IdAndGuild_OwnerIdAndBannedIsTrue(Long guildId, Long ownerId);
    Optional<UserGuildEntity> findByUserIdAndGuild_IdAndBannedIsFalse(Long userId, Long guildId);
    Optional<UserGuildEntity> findByUserIdAndGuild_IdAndBannedIsTrue(Long userId, Long guildId);
    boolean existsByUserIdAndGuild_Id(Long userId, Long guildId);
    void deleteAllByGuild_Id(Long guildId);
}
