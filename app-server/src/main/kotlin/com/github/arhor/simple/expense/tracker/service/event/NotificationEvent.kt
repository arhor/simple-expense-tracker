package com.github.arhor.simple.expense.tracker.service.event

import com.github.arhor.simple.expense.tracker.model.NotificationDTO

data class NotificationEvent(
    val senderId: Long,
    val userId: Long,
    val notification: NotificationDTO,
)
