package com.github.arhor.simple.expense.tracker.service.event

import com.github.arhor.simple.expense.tracker.model.NotificationDTO

data class NotificationEvent(
    val sourceUserId: Long,
    val targetUserId: Long,
    val notification: NotificationDTO,
)
