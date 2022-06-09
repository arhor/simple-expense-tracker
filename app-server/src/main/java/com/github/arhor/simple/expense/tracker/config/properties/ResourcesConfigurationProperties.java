package com.github.arhor.simple.expense.tracker.config.properties;

import lombok.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
@ConfigurationProperties("configuration.resources")
public class ResourcesConfigurationProperties {
    /**
     * Path patterns for the static resources.
     */
    String[] patterns;

    /**
     * Locations to lookup for the static resources.
     */
    String[] locations;
}
