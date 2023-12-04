/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.settings.domain.userrelation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelationModel, Long> {
    Optional<UserRelationModel> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
