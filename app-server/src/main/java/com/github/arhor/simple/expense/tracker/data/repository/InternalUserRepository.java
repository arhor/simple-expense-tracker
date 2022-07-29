package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

public interface InternalUserRepository extends CrudRepository<InternalUser, Long> {

    Optional<InternalUser> findInternalUserByUsername(String username);

    @Query(name = "InternalUser.findByUsername")
    Optional<InternalUser.CompactProjection> findByUsername(String username);

    @Query(name = "InternalUser.findByExternalIdAndProvider")
    Optional<InternalUser.CompactProjection> findByExternalIdAndProvider(String externalId, String externalProvider);

    boolean existsByUsername(String username);

    boolean existsByExternalIdAndExternalProvider(String externalId, String externalProvider);
}
