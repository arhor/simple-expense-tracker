package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;

import static org.springframework.web.method.HandlerTypePredicate.forAnnotation;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebServerConfig implements WebMvcConfigurer {

    private final ApplicationProps applicationProps;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
    }

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        applicationProps.apiPathPrefix().ifPresent(pathPrefix -> {
            configurer.addPathPrefix(pathPrefix, forAnnotation(RestController.class));
        });
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        applicationProps.resources().ifPresent(it -> {
            val patterns = it.patterns().toArray(String[]::new);
            val location = it.location().toArray(String[]::new);

            registry.addResourceHandler(patterns).addResourceLocations(location);
        });
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourcePatternResolver resourcePatternResolver(final ResourceLoader resourceLoader) {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }
}
