package com.github.arhor.simple.expense.tracker.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @param patterns  path patterns for the static resources
 * @param locations locations to lookup for the static resources
 */
@ConstructorBinding
@ConfigurationProperties("configuration.resources")
public record ResourcesConfigurationProperties(String[] patterns, String[] locations) {
}
