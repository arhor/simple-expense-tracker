package com.github.arhor.simple.expense.tracker.data.model.projection

import java.util.*

/**
 * Compact projection of the  [com.github.arhor.simple.expense.tracker.data.model.Notification] entity.
 * Main purpose is to load only necessary fields from DB.
 */
data class CompactNotificationProjection(
    val id: UUID,
    val targetUserId: Long,
    val message: String,
    val severity: String,
)
