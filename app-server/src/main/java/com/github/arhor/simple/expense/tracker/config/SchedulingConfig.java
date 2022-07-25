package com.github.arhor.simple.expense.tracker.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "configuration.notifications.enabled", havingValue = "true")
public class SchedulingConfig {
}
