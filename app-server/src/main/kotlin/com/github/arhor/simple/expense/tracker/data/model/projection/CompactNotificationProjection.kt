package com.github.arhor.simple.expense.tracker.data.model.projection

import java.util.UUID

/**
 * Compact projection of the  [com.github.arhor.simple.expense.tracker.data.model.Notification] entity.
 * Main purpose is to load only necessary fields from DB.
 */
data class CompactNotificationProjection(
    val id: UUID,
    val severity: String,
    val message: String,
    val targetUserId: Long,
)
