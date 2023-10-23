/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.user;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Cacheable(cacheNames = "user_by_id")
    Optional<UserEntity> findById(Long id);

    @Query(value = "from UserEntity u join fetch u.roles where u.username = :identity or u.emailAddress = :identity")
    @Cacheable(cacheNames = "user_by_username_or_email")
    Optional<UserEntity> findByUsernameOrEmailAddress(@Param("identity") String identity);

    @Cacheable(cacheNames = "user_exist_by_username_or_email")
    boolean existsByUsernameOrEmailAddress(String username, String emailAddress);
}
