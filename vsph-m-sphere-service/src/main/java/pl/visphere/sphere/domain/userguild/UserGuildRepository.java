/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.userguild;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGuildRepository extends JpaRepository<UserGuildEntity, Long> {
    List<UserGuildEntity> findAllByUserId(Long userId);
}
