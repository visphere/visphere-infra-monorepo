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
    List<UserGuildEntity> findAllByUserId(Long userId);
    List<UserGuildEntity> findAllByGuild_Id(Long guildId);
    Optional<UserGuildEntity> findByUserIdAndGuild_Id(Long userId, Long guildId);
    int countAllByGuild_Id(Long guildId);
    boolean existsByUserIdAndGuild_Id(Long userId, Long guildId);
    void deleteAllByUserId(Long userId);
}
