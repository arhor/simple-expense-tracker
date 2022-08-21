package com.github.arhor.simple.expense.tracker

import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import java.lang.invoke.MethodHandles

private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackageClasses = [ApplicationProps::class])
class Application(private val env: Environment) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val port = env.getProperty("server.port")
        val path = env.getProperty("server.servlet.context-path", "/")

        if (port != null) {
            log.info("Local access URL: http://localhost:{}{}", port, path)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
