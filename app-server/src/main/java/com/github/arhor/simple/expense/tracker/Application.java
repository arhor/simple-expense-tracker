package com.github.arhor.simple.expense.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.github.arhor.simple.expense.tracker.config.properties.ConfigPropsPackage;

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackageClasses = ConfigPropsPackage.class)
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
