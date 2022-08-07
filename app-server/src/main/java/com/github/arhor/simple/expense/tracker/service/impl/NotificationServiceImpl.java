package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.github.arhor.simple.expense.tracker.data.repository.NotificationRepository;
import com.github.arhor.simple.expense.tracker.model.NotificationDTO;
import com.github.arhor.simple.expense.tracker.service.NotificationService;
import com.github.arhor.simple.expense.tracker.service.event.NotificationEvent;
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFICATION_EVENT = "notification-event";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private final Map<Long, SseEmitter> subscribers = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(final Long subscriberId) {
        return subscribers.computeIfAbsent(subscriberId, SseEmitter::new);
    }

    @Override
    public void unsubscribe(final Long subscriberId) {
        subscribers.remove(subscriberId);
    }

    @Override
    public void handleNotification(final Long senderId, final Long userId, final NotificationDTO dto) {
        applicationEventPublisher.publishEvent(
            new NotificationEvent(senderId, userId, dto)
        );
    }

    @Override
    @Transactional
    public void sendNotification(final Long senderId, final Long userId, final NotificationDTO dto) throws IOException {
        var sent = false;
        try {
            sent = sendInternal(userId, dto);
        } finally {
            if (!sent) {
                val notification = notificationMapper.mapDtoToEntity(dto, userId, senderId);
                notificationRepository.save(notification);
            }
        }
    }

    @Override
    @Transactional
    public void sendNotifications() throws IOException {
        val userIds = subscribers.keySet();

        if (!userIds.isEmpty()) {
            val notifications = notificationRepository.findAllByTargetUserIdIn(userIds);

            for (val notification : notifications) {
                val sent =
                    sendInternal(
                        notification.targetUserId(),
                        notificationMapper.mapProjectionToDto(notification)
                    );

                if (sent) {
                    notificationRepository.deleteById(notification.id());
                }
            }
        }
    }

    private boolean sendInternal(final Long userId, final NotificationDTO notification) throws IOException {
        val emitter = subscribers.get(userId);

        if (emitter != null) {
            val eventId = UUID.randomUUID().toString();

            emitter.send(
                SseEmitter.event()
                    .id(eventId)
                    .name(NOTIFICATION_EVENT)
                    .data(notification)
            );
            return true;
        }
        return false;
    }
}
