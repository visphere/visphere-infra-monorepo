/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.otatoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.visphere.lib.security.OtaToken;

import java.util.Optional;

@Repository
public interface OtaTokenRepository extends JpaRepository<OtaTokenEntity, Long> {
    boolean existsByToken(String token);
    Optional<OtaTokenEntity> findByTokenAndTypeAndIsUsedFalse(String token, OtaToken type);
}
