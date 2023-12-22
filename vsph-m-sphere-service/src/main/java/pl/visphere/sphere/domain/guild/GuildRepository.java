/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guild;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<GuildEntity, Long> {
    Optional<GuildEntity> findByIdAndOwnerId(Long id, Long ownerId);
    Optional<GuildEntity> findByIdAndOwnerIdAndIsPrivateIsTrue(Long id, Long ownerId);
    Optional<GuildEntity> findByIdAndIsPrivateIsFalse(Long id);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
