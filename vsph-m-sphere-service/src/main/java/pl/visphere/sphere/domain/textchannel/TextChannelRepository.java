/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.textchannel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TextChannelRepository extends JpaRepository<TextChannelEntity, Long> {
    List<TextChannelEntity> findAllByGuild_Id(Long guildId);
    List<TextChannelEntity> findAllByGuild_IdIn(List<Long> guildIds);
    Optional<TextChannelEntity> findByIdAndGuild_OwnerId(Long textChannelId, Long ownerId);
    boolean existsByGuild_IdAndName(Long guildId, String name);
    boolean existsByGuild_IdAndNameAndIdNot(Long guildId, String name, Long textChannelId);
}
