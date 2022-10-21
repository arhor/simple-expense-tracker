package com.github.arhor.simple.expense.tracker.service.event

import com.github.arhor.simple.expense.tracker.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.invoke.MethodHandles

@Service
class NotificationEventListener(private val service: NotificationService) {

    @Async
    @Transactional
    @EventListener(NotificationEvent::class)
    fun handleNotificationEvent(event: NotificationEvent) {
        val userId = event.userId
        val senderId = event.senderId
        val notification = event.notification

        log.debug(
            "Handled notification event: {}, for the user: {}, from user: {}",
            notification,
            userId,
            senderId
        )
        service.sendNotification(senderId, userId, notification)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
