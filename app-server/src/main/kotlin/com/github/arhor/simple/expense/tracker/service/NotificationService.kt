package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface NotificationService {

    fun subscribe(subscriberId: Long): SseEmitter

    fun unsubscribe(subscriberId: Long)

    fun unsubscribeAll()

    fun handleNotification(sourceUserId: Long, targetUserId: Long, notification: NotificationDTO)

    fun sendNotification(sourceUserId: Long, targetUserId: Long, notification: NotificationDTO)

    fun sendNotifications()
}
