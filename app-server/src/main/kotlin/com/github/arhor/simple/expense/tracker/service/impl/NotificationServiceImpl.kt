package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.repository.NotificationRepository
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.NotificationService
import com.github.arhor.simple.expense.tracker.service.event.NotificationEvent
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper
import com.github.arhor.simple.expense.tracker.util.currentLocalDateTime
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
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

    override fun unsubscribeAll() {
        subscribers.clear()
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
            sent = sendInternal(userId = userId, notification = dto)
        } finally {
            if (!sent) {
                val notification = notificationMapper.mapDtoToEntity(
                    dto = dto,
                    targetUserId = userId,
                    sourceUserId = senderId,
                    timestamp = currentLocalDateTime(),
                )
                notificationRepository.save(notification)
            }
        }
    }

    @Transactional
    override fun sendNotifications() {
        val userIds = subscribers.keys

        if (!userIds.isEmpty()) {
            notificationRepository.findAllByTargetUserIdIn(userIds).use { notifications ->
                notifications.forEach {
                    var sent = false
                    try {
                        sent = sendInternal(
                            userId = it.targetUserId,
                            notification = notificationMapper.mapProjectionToDto(it)
                        )
                    } finally {
                        if (sent) {
                            notificationRepository.deleteById(it.id)
                        }
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
