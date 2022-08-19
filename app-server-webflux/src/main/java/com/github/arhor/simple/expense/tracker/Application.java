package com.github.arhor.simple.expense.tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.github.arhor.simple.expense.tracker.web.controller.SimpleController;

@Slf4j
@SpringBootApplication(proxyBeanMethods = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Application {

    private final SimpleController simpleController;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(final Environment env) {
        return args -> {
            val port = env.getProperty("server.port");
            val path = env.getProperty("server.webflux.base-path", "/");

            log.info("Local access URL: http://localhost:{}{}", port, path);
        };
    }
}
