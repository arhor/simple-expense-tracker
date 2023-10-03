package com.github.arhor.simple.expense.tracker.data.model.projection

/**
 * Compact projection of the [com.github.arhor.simple.expense.tracker.data.model.InternalUser] entity.
 * Main purpose is to load only necessary fields from DB.
 */
data class CompactInternalUserProjection(
    val id: Long,
    val username: String?,
    val currency: String,
)
