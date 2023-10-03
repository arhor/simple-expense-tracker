package com.github.arhor.simple.expense.tracker.data.model;

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("notifications")
@Immutable
data class Notification(
    @Id
    val id: UUID? = null,
    val message: String,
    val severity: String,
    val sourceUserId: Long,
    val targetUserId: Long,
    val timestamp: LocalDateTime,
)
