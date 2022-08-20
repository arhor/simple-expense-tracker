package com.github.arhor.simple.expense.tracker.service.mapping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(MapperTestBase.Config.class)
abstract class MapperTestBase {

    @MockBean
    protected PasswordEncoder passwordEncoder;

    @ComponentScan
    @TestConfiguration
    static class Config {}
}
