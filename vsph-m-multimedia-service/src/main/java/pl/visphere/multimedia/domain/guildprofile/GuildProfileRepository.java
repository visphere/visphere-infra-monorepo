/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.domain.guildprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildProfileRepository extends JpaRepository<GuildProfileEntity, Long> {
    Optional<GuildProfileEntity> findByGuildId(Long guildId);
    List<GuildProfileEntity> findAllByGuildIdIn(List<Long> guildIds);
    void deleteByGuildId(Long guildId);
}
