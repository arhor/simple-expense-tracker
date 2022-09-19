package com.github.arhor.simple.expense.tracker

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.context.WebApplicationContext
import java.lang.invoke.MethodHandles

private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackageClasses = [ApplicationProps::class])
class Application {

    @Bean
    @Profile("dev")
    fun <T> displayApplicationInfo(context: T)
        where T : WebApplicationContext,
              T : WebServerApplicationContext = ApplicationRunner {

        val port = context.webServer.port
        val path = context.servletContext?.contextPath ?: ""

        log.info("Local access URL: http://localhost:{}{}", port, path)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
