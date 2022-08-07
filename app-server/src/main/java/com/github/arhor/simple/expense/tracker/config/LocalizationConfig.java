package com.github.arhor.simple.expense.tracker.config;

import lombok.val;

import java.util.Locale;
import java.util.Optional;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration(proxyBeanMethods = false)
public class LocalizationConfig {

    @Bean
    public MessageSource messageSource(final Optional<WebProperties> webProperties) {
        val messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(webProperties.map(WebProperties::getLocale).orElse(Locale.ENGLISH));

        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(final MessageSource messages) {
        val validatorFactoryBean = new LocalValidatorFactoryBean();

        validatorFactoryBean.setValidationMessageSource(messages);

        return validatorFactoryBean;
    }
}
