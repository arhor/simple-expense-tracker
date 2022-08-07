package com.github.arhor.simple.expense.tracker.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.arhor.simple.expense.tracker.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationEventListener {

    private final NotificationService service;

    @Async
    @Transactional
    @EventListener(NotificationEvent.class)
    public void handleNotificationEvent(final NotificationEvent event) throws IOException {
        val userId = event.userId();
        val senderId = event.senderId();
        val notification = event.notification();

        log.debug("Handled notification event: {}, for the user: {}, from user: {}", notification, userId, senderId);

        service.sendNotification(senderId, userId, notification);
    }
}
