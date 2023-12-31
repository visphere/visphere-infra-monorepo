/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.multimedia.domain.accountprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfileEntity, Long> {
    Optional<AccountProfileEntity> findByUserId(Long userId);
    List<AccountProfileEntity> findAllByUserIdIn(List<Long> userIds);
    void deleteByUserId(Long userId);
}
