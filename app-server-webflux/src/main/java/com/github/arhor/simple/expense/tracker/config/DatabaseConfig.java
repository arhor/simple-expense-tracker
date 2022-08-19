package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.github.arhor.simple.expense.tracker.util.TimeUtils.currentZonedDateTime;

@Configuration(proxyBeanMethods = false)
@EnableR2dbcAuditing(modifyOnCreate = false, dateTimeProviderRef = "instantDateTimeProviderUTC")
@EnableR2dbcRepositories(basePackages = "com.github.arhor.simple.expense.tracker.data.repository")
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

    @Bean
    @Primary
    public R2dbcProperties r2dbcProperties() {
        val r2dbc = new R2dbcProperties();

        r2dbc.setUrl(dbUrl.replaceFirst("jdbc", "r2dbc"));
        r2dbc.setUsername(dbUsername);
        r2dbc.setPassword(dbPassword);

        return r2dbc;
    }
}
