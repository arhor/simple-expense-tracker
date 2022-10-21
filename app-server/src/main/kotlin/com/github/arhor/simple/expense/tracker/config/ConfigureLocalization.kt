package com.github.arhor.simple.expense.tracker.config

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.util.*

@Configuration(proxyBeanMethods = false)
class ConfigureLocalization {

    @Bean
    fun messageSource(webProperties: Optional<WebProperties>): MessageSource {
        return ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
            setDefaultLocale(webProperties.map { it.locale }.orElse(Locale.ENGLISH))
        }
    }

    @Bean
    fun localValidatorFactoryBean(messages: MessageSource): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messages)
        }
    }
}
