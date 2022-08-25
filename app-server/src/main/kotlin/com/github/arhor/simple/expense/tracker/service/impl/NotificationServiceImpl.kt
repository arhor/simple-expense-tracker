package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.repository.NotificationRepository
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.NotificationService
import com.github.arhor.simple.expense.tracker.service.event.NotificationEvent
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationServiceImpl(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val notificationRepository: NotificationRepository,
    private val notificationMapper: NotificationMapper,
) : NotificationService {

    private val subscribers = ConcurrentHashMap<Long, SseEmitter>()

    override fun subscribe(subscriberId: Long): SseEmitter {
        return subscribers.computeIfAbsent(subscriberId) { SseEmitter() }
    }

    override fun unsubscribe(subscriberId: Long) {
        subscribers.remove(subscriberId)
    }

    override fun handleNotification(senderId: Long, userId: Long, dto: NotificationDTO) {
        applicationEventPublisher.publishEvent(
            NotificationEvent(senderId, userId, dto)
        )
    }

    @Transactional
    override fun sendNotification(senderId: Long, userId: Long, dto: NotificationDTO) {
        var sent = false
        try {
            sent = sendInternal(userId, dto)
        } finally {
            if (!sent) {
                val notification = notificationMapper.mapDtoToEntity(dto, userId, senderId)
                notificationRepository.save(notification)
            }
        }
    }

    @Transactional
    override fun sendNotifications() {
        val userIds = subscribers.keys

        if (!userIds.isEmpty()) {
            val notifications = notificationRepository.findAllByTargetUserIdIn(userIds)

            for (notification in notifications) {
                var sent = false
                try {
                    sent = sendInternal(
                        notification.targetUserId,
                        notificationMapper.mapProjectionToDto(notification)
                    )
                } finally {
                    if (sent) {
                        notificationRepository.deleteById(notification.id)
                    }
                }
            }
        }
    }

    private fun sendInternal(userId: Long, notification: NotificationDTO): Boolean {
        val emitter = subscribers[userId]

        if (emitter != null) {
            val eventId = UUID.randomUUID().toString()

            emitter.send(
                SseEmitter.event()
                    .id(eventId)
                    .name(NOTIFICATION_EVENT)
                    .data(notification)
            )
            return true
        }
        return false
    }

    companion object {
        private const val NOTIFICATION_EVENT = "notification-event"
    }
}
