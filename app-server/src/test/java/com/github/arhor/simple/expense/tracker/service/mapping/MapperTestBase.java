package com.github.arhor.simple.expense.tracker.service.mapping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.Mockito.mock;

@SpringJUnitConfig(MapperTestBase.MapperTestConfig.class)
abstract class MapperTestBase {

    @TestConfiguration
    @ComponentScan("com.github.arhor.simple.expense.tracker.service.mapping")
    static class MapperTestConfig {

        @Bean
        PasswordEncoder mockedPasswordEncoder() {
            return mock(PasswordEncoder.class);
        }
    }
}
