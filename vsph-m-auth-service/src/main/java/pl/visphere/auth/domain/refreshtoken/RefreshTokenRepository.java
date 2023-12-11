/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshTokenAndUserId(String refreshToken, Long userId);
    List<RefreshTokenEntity> findAllByUser_Id(Long userId);
    List<RefreshTokenEntity> findAllByUser_IdAndRefreshTokenNot(Long userId, String refreshToken);
}
