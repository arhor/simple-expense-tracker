package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "instantDateTimeProviderUTC")
@EnableJdbcRepositories(basePackages = "com.github.arhor.simple.expense.tracker.data.repository")
@EnableTransactionManagement
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatabaseConfig {

    private final Environment env;

    @Bean
    public DateTimeProvider instantDateTimeProviderUTC() {
        return () -> Optional.of(Clock.system(ZoneOffset.UTC).instant().truncatedTo(ChronoUnit.MILLIS));
    }

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true")
    public Flyway flyway() {
        log.debug("Configuring flyway instance to apply migrations");

        var flywayConfig = Flyway.configure().baselineOnMigrate(true)
            .dataSource(env.getRequiredProperty("spring.flyway.url"), env.getRequiredProperty("spring.flyway.user"),
                env.getRequiredProperty("spring.flyway.password"));

        var locations = env.getProperty("spring.flyway.locations");

        if (locations != null && locations.length() > 0) {
            flywayConfig.locations(locations.split(","));
        }

        return flywayConfig.load();
    }
}
