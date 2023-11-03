/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guildlink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildLinkRepository extends JpaRepository<GuildLinkEntity, Long> {
    @Modifying
    void removeAllByGuild_Id(Long guildId);
    boolean existsByToken(String token);
    List<GuildLinkEntity> findByGuild_Id(Long guildId);
    Optional<GuildLinkEntity> findByIdAndGuild_OwnerId(Long linkId, Long ownerId);
}
