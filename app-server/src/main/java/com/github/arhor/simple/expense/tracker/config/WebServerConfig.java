package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.arhor.simple.expense.tracker.config.properties.ResourcesConfigurationProperties;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebServerConfig implements WebMvcConfigurer {

    @Value("${configuration.api-path-prefix:/api}")
    private final String apiPathPrefix;
    private final Optional<ResourcesConfigurationProperties> resources;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
    }

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiPathPrefix, HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        resources.ifPresent(it -> {
            val patterns = it.patterns().toArray(String[]::new);
            val location = it.location().toArray(String[]::new);

            registry.addResourceHandler(patterns).addResourceLocations(location);
        });
    }
}
