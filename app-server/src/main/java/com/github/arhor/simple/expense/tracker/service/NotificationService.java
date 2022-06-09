package com.github.arhor.simple.expense.tracker.service;

import java.io.IOException;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.github.arhor.simple.expense.tracker.model.NotificationDTO;

public interface NotificationService {

    SseEmitter subscribe(Long subscriberId);

    void unsubscribe(Long subscriberId);

    void handleNotification(Long senderId, Long userId, NotificationDTO notification);

    void sendNotification(Long senderId, Long userId, NotificationDTO notification) throws IOException;

    void sendNotifications() throws Exception;
}
