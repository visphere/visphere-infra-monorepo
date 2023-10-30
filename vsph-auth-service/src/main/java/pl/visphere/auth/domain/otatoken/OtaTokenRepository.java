/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.otatoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.visphere.lib.security.OtaToken;

import java.util.Optional;

@Repository
public interface OtaTokenRepository extends JpaRepository<OtaTokenEntity, Long> {
    boolean existsByToken(String token);

    @Query(value = """
            from OtaTokenEntity e join fetch e.user u where e.token = :token
            and e.expiredAt > current_timestamp()
            and e.isUsed = false
            and e.type = :type
            and u.id = :userId
        """)
    Optional<OtaTokenEntity> findTokenByType(
        @Param("token") String token,
        @Param("type") OtaToken type,
        @Param("userId") Long userId
    );
}
