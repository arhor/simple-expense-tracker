package com.github.arhor.simple.expense.tracker;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;

@Slf4j
@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackageClasses = ApplicationProps.class)
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(final Environment env) {
        return args -> {
            val port = env.getProperty("server.port");
            val path = env.getProperty("server.servlet.context-path", "/");

            if ((port != null) && (path != null)) {
                log.info("Local access URL: http://localhost:{}{}", port, path);
            }
        };
    }
}
