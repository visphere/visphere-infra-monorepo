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
    Optional<UserEntity> findByIdAndExternalCredProviderIsTrue(Long id);
    List<UserEntity> findAllByUsernameInOrEmailAddressIn(List<String> usernames, List<String> emailAddresses);
    boolean existsByUsernameOrEmailAddress(String username, String emailAddress);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdIsNot(String username, Long id);
    boolean existsByEmailAddress(String emailAddress);

    @Query(value = """
            from UserEntity u join fetch u.roles
            where u.username = :identity or u.emailAddress = :identity and u.externalCredProvider = false
        """)
    Optional<UserEntity> findByLocalUsernameOrEmailAddress(@Param("identity") String identity);

    @Query(value = "from UserEntity u join fetch u.roles where u.username = :identity or u.emailAddress = :identity")
    Optional<UserEntity> findByUsernameOrEmailAddress(@Param("identity") String identity);

    @Query(value = "from UserEntity u join fetch u.roles where u.id = :id")
    Optional<UserEntity> findByIdFetchRoles(@Param("id") Long id);
}
