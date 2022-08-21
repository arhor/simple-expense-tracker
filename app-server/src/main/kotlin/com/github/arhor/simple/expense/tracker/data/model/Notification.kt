package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("notifications")
@Builder(toBuilder = true)
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
