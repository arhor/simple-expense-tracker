package com.github.arhor.simple.expense.tracker;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

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
}
