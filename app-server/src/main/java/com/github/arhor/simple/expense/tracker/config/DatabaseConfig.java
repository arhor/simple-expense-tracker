package com.github.arhor.simple.expense.tracker.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.github.arhor.simple.expense.tracker.util.TimeUtils.currentZonedDateTime;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "instantDateTimeProviderUTC")
@EnableJdbcRepositories(basePackages = "com.github.arhor.simple.expense.tracker.data.repository")
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    public DateTimeProvider instantDateTimeProviderUTC() {
        return () -> Optional.of(currentZonedDateTime());
    }

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true")
    public Flyway flyway(final Environment env) {
        log.debug("Configuring flyway instance to apply migrations");

        val dbUrl = env.getRequiredProperty("spring.flyway.url");
        val dbUsername = env.getRequiredProperty("spring.flyway.user");
        val dbPassword = env.getRequiredProperty("spring.flyway.password");

        val flywayConfig = Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(dbUrl, dbUsername, dbPassword);

        val locations = env.getProperty("spring.flyway.locations");

        if ((locations != null) && locations.length() > 0) {
            flywayConfig.locations(locations.split(","));
        }

        return flywayConfig.load();
    }
}
