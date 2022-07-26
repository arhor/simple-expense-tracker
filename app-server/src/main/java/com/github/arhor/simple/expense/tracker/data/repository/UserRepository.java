package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

public interface UserRepository extends CrudRepository<InternalUser, Long> {

    Optional<InternalUser> findByUsername(String username);

    Optional<InternalUser> findByExternalIdAndExternalProvider(String externalId, String externalProvider);

    boolean existsByUsername(String username);

    boolean existsByExternalIdAndExternalProvider(String externalId, String externalProvider);
}
