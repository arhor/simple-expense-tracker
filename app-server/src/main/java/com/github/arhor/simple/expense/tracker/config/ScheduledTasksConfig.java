package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.arhor.simple.expense.tracker.service.NotificationService;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "application-props", name = "enable-scheduled-tasks")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasksConfig {

    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 30, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void findAndSendNotificationsToSubscribers() throws Exception {
        notificationService.sendNotifications();
    }
}
