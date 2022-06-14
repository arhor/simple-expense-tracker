package com.github.arhor.simple.expense.tracker;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.web.client.RestTemplate;

import com.github.arhor.simple.expense.tracker.task.startup.StartupTask;

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan("com.github.arhor.simple.expense.tracker.config")
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(final List<StartupTask> startupTasks) {
        return args -> {
            for (var task : startupTasks) {
                task.execute();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourcePatternResolver resourcePatternResolver(final ResourceLoader resourceLoader) {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }
}
