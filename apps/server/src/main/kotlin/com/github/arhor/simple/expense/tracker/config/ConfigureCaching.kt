package com.github.arhor.simple.expense.tracker.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching
