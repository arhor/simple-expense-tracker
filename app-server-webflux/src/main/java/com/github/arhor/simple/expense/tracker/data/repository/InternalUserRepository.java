package com.github.arhor.simple.expense.tracker.data.repository;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

public interface InternalUserRepository extends ReactiveCrudRepository<InternalUser, Long> {

    Mono<InternalUser> findInternalUserByUsername(String username);

    // language=SQL
    @Query("""
        SELECT u.id
             , u.username
             , u.currency
          FROM users u
         WHERE u.username = :username
        """)
    Mono<InternalUser.Projection> findByUsername(String username);

    // language=SQL
    @Query("""
        SELECT u.id
             , u.username
             , u.currency
          FROM users u
         WHERE u.external_id = :externalId
           AND u.external_provider = :externalProvider
        """)
    Mono<InternalUser.Projection> findByExternalIdAndProvider(String externalId, String externalProvider);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByExternalIdAndExternalProvider(String externalId, String externalProvider);
}
