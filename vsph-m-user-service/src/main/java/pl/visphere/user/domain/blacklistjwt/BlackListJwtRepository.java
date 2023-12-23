/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.domain.blacklistjwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListJwtRepository extends JpaRepository<BlackListJwtEntity, Long> {
    boolean existsByExpiredJwt(String expiredJwt);
}
