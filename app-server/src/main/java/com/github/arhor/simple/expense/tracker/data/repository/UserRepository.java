package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

@Repository
public interface UserRepository extends BaseRepository<InternalUser, Long> {

    @Query("SELECT u.* FROM users u WHERE u.deleted = FALSE AND u.username = :username")
    Optional<InternalUser> findByUsername(String username);

    @Query("""
        SELECT u.*
        FROM users u
        WHERE u.deleted = FALSE
          AND u.external_id = :externalId
          AND u.external_provider = :externalProvider""")
    Optional<InternalUser> findByExternalAttributes(String externalId, String externalProvider);

    @Override
    @Modifying
    @Query("UPDATE users SET deleted = TRUE WHERE id IN (:ids)")
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    @Modifying
    @Query("UPDATE users SET deleted = TRUE WHERE id = :id")
    void deleteById(Long id);

    @Override
    @Query("SELECT u.* FROM users u WHERE u.deleted = FALSE")
    List<InternalUser> findAll();

    @Query("SELECT u.* FROM users u WHERE u.deleted = FALSE LIMIT :size OFFSET :#{#page * #size}")
    List<InternalUser> findAll(int page, int size);

    @Override
    @Query("SELECT u.* FROM users u WHERE u.id IN (:ids) AND u.deleted = FALSE")
    List<InternalUser> findAllById(Iterable<Long> ids);

    @Override
    @Query("SELECT u.* FROM users u WHERE u.id = :id AND u.deleted = FALSE")
    Optional<InternalUser> findById(Long id);

    @Override
    @Query("SELECT COUNT(u) FROM users u WHERE u.deleted = FALSE")
    long count();

    @Override
    @Query("""
        SELECT
            CASE WHEN COUNT(u) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM users u
        WHERE u.deleted = FALSE
          AND u.id = :id""")
    boolean existsById(Long id);

    @Query("""
        SELECT
            CASE WHEN COUNT(u) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM users u
        WHERE u.deleted = FALSE
          AND u.username = :username""")
    boolean existsByUsername(String username);

    @Query("""
        SELECT
            CASE WHEN COUNT(u) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM users u
        WHERE u.deleted = FALSE
          AND u.external_id = :externalId
          AND u.external_provider = :externalProvider""")
    boolean existsByExternalIdAndExternalProvider(String externalId, String externalProvider);

    @Override
    @Modifying
    @Query("UPDATE users SET deleted = TRUE WHERE deleted = FALSE")
    void deleteAll();
}
