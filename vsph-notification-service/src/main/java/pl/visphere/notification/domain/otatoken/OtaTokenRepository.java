/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.domain.otatoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtaTokenRepository extends JpaRepository<OtaTokenEntity, Long> {
    boolean existsByToken(String token);
}
