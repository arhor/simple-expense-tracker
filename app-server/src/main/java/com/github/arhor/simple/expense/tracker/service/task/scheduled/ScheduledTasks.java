package com.github.arhor.simple.expense.tracker.service.task.scheduled;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.service.NotificationService;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasks {

    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 30, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void findAndSendNotificationsToSubscribers() throws Exception {
        notificationService.sendNotifications();
    }
}
