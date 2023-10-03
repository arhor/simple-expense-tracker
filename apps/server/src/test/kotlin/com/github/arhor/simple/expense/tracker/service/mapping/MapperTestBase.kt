package com.github.arhor.simple.expense.tracker.service.mapping

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig(MapperTestBase.Config::class)
internal abstract class MapperTestBase {

    @ComponentScan
    @TestConfiguration
    class Config
}
