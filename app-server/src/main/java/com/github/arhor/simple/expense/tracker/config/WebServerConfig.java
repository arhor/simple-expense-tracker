package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String API_PATH_PREFIX = "/api";

    private final Optional<ResourcesConfigurationProperties> resources;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
    }

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX, HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        resources.ifPresent(it -> {
            registry.addResourceHandler(it.patterns()).addResourceLocations(it.locations());
        });
    }

    public static String apiUrlPath(final String urlPath) {
        return API_PATH_PREFIX + urlPath;
    }
}
