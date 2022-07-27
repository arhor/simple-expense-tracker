package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

public interface UserRepository extends CrudRepository<InternalUser, Long> {

    Optional<InternalUser> findInternalUserByUsername(String username);

    @Query("""
        SELECT u.id
             , u.username
             , u.currency
        FROM users u
        WHERE u.username = :username
        """)
    Optional<InternalUser.CompactProjection> findByUsername(String username);

    @Query("""
        SELECT u.id
             , u.username
             , u.currency
        FROM users u
        WHERE u.external_id = :externalId
          AND u.external_provider = :externalProvider
        """)
    Optional<InternalUser.CompactProjection> findByExternalIdAndProvider(String externalId, String externalProvider);

    boolean existsByUsername(String username);

    boolean existsByExternalIdAndExternalProvider(String externalId, String externalProvider);
}
