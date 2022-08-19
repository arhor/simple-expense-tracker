package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.github.arhor.simple.expense.tracker.util.TimeUtils.currentZonedDateTime;

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "instantDateTimeProviderUTC")
@EnableJdbcRepositories(basePackages = "com.github.arhor.simple.expense.tracker.data.repository")
@EnableTransactionManagement
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private final String dbUrl;

    @Value("${spring.datasource.username}")
    private final String dbUsername;

    @Value("${spring.datasource.password}")
    private final String dbPassword;

    @Bean
    public DateTimeProvider instantDateTimeProviderUTC() {
        return () -> Optional.of(currentZonedDateTime());
    }

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true")
    public Flyway flyway() {
        return Flyway.configure()
            .dataSource(dbUrl, dbUsername, dbPassword)
            .baselineOnMigrate(true)
            .baselineVersion("0.0")
            .loggers("slf4j")
            .load();
    }
}
