package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.service.NotificationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "application-props", name = ["enable-scheduled-tasks"])
class ConfigureScheduledTasks(private val notificationService: NotificationService) {

    @Scheduled(fixedDelay = 30, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    fun findAndSendNotificationsToSubscribers() {
        notificationService.sendNotifications()
    }
}
