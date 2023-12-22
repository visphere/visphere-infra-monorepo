/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guildlink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuildLinkRepository extends JpaRepository<GuildLinkEntity, Long> {
    List<GuildLinkEntity> findAllByGuild_IdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(Long guildId, Long ownerId);
    Optional<GuildLinkEntity> findByIdAndGuild_OwnerIdAndGuild_IsPrivateIsTrue(Long linkId, Long ownerId);
    Optional<GuildLinkEntity> findByTokenAndGuild_IsPrivateIsTrueAndExpiredAtAfterAndIsActiveIsTrue(String token, ZonedDateTime now);
    boolean existsByToken(String token);
    void deleteAllByGuild_Id(Long guildId);
}
