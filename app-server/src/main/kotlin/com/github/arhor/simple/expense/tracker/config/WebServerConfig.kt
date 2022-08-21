package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerTypePredicate
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class WebServerConfig(private val applicationProps: ApplicationProps) : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/")
    }

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        applicationProps.apiPathPrefix?.let {
            configurer.addPathPrefix(it, HandlerTypePredicate.forAnnotation(RestController::class.java))
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        applicationProps.resources?.let {
            val patterns = it.patterns.toTypedArray()
            val location = it.location.toTypedArray()

            registry.addResourceHandler(*patterns).addResourceLocations(*location)
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun resourcePatternResolver(resourceLoader: ResourceLoader): ResourcePatternResolver {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
    }
}
