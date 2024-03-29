package com.github.arhor.simple.expense.tracker.service.event

import com.github.arhor.simple.expense.tracker.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class NotificationEventListener(
    private val service: NotificationService,
) {

    @Async
    @EventListener
    fun handleNotificationEvent(event: NotificationEvent) {
        event.let { (sourceUserId, targetUserId, notification) ->
            log.debug(
                "Handled notification event: source user: {}, target user: {}",
                sourceUserId,
                targetUserId,
            )
            service.sendNotification(sourceUserId, targetUserId, notification)
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(NotificationEventListener::class.java)
    }
}
