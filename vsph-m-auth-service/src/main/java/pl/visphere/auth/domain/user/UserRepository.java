/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByIdAndIsActivatedIsTrue(Long id);

    @Query(value = "from UserEntity u join fetch u.roles where u.username = :identity or u.emailAddress = :identity")
    Optional<UserEntity> findByUsernameOrEmailAddress(@Param("identity") String identity);

    boolean existsByUsernameOrEmailAddress(String username, String emailAddress);
    boolean existsByUsername(String username);
    boolean existsByEmailAddress(String emailAddress);
    List<UserEntity> findAllByUsernameInOrEmailAddressIn(List<String> usernames, List<String> emailAddresses);
}
