package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.ApplicationProfiles
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching {

    @Bean
    fun caffeineCacheBuilder(env: Environment): Caffeine<*, *> {
        val cacheBuilder = Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterAccess(10, TimeUnit.MINUTES)

        if (env.activeProfiles.contains(ApplicationProfiles.DEV)) {
            cacheBuilder.recordStats()
        }
        return cacheBuilder
    }
}
