package com.github.arhor.simple.expense.tracker;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@SpringJUnitConfig(ContextTest.EmptyContextConfig.class)
class ContextTest {

    @Configuration(proxyBeanMethods = false)
    static class EmptyContextConfig {
    }

    @Autowired
    private ApplicationContext ctx;

    @Test
    void application_context_should_be_loaded_successfully() {
        assertThat(ctx)
            .isNotNull();
    }
}
