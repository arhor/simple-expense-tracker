package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface InternalUserRepository : CrudRepository<InternalUser, Long> {

    fun findInternalUserByUsername(username: String): InternalUser?

    @Query(name = "InternalUser.findByUsername")
    fun findByUsername(username: String): CompactInternalUserProjection?

    @Query(name = "InternalUser.findByExternalIdAndProvider")
    fun findByExternalIdAndProvider(externalId: String, externalProvider: String): CompactInternalUserProjection

    fun existsByUsername(username: String): Boolean

    fun existsByExternalIdAndExternalProvider(externalId: String, externalProvider: String): Boolean
}
