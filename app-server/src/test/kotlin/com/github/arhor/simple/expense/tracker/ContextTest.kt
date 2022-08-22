package com.github.arhor.simple.expense.tracker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig(ContextTest.EmptyContextConfig::class)
internal class ContextTest {

    @TestConfiguration(proxyBeanMethods = false)
    class EmptyContextConfig

    @Autowired
    private lateinit var ctx: ApplicationContext

    @Test
    fun `application context should be loaded successfully`() {
        assertThat(ctx)
            .isNotNull
    }
}
