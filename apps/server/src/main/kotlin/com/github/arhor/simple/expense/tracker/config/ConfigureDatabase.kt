package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.service.util.currentZonedDateTime
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Optional

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "instantDateTimeProviderUTC")
@EnableJdbcRepositories(basePackages = ["com.github.arhor.simple.expense.tracker.data.repository"])
@EnableTransactionManagement
class ConfigureDatabase {

    @Autowired
    private lateinit var dbProps: DataSourceProperties

    @Bean
    fun instantDateTimeProviderUTC() = DateTimeProvider { Optional.of(currentZonedDateTime()) }

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = ["spring.flyway.enabled"], havingValue = "true")
    fun flyway(): Flyway {
        return Flyway.configure()
            .dataSource(dbProps.url, dbProps.username, dbProps.password)
            .baselineOnMigrate(true)
            .baselineVersion("0.0")
            .loggers("slf4j")
            .load()
    }
}
