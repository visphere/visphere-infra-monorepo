/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.oauth2.domain.oauth2user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.visphere.oauth2.core.OAuth2Supplier;

import java.util.Optional;

@Repository
public interface OAuth2UserRepository extends JpaRepository<OAuth2UserEntity, Long> {
    Optional<OAuth2UserEntity> findByProviderIdAndSupplier(String providerId, OAuth2Supplier supplier);
    Optional<OAuth2UserEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
