package com.github.arhor.simple.expense.tracker.config.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @param patterns  path patterns for the static resources
 * @param location locations to lookup for the static resources
 */
@ConstructorBinding
@ConfigurationProperties("configuration.resources")
public record ResourcesConfigurationProperties(List<String> patterns, List<String> location) {
}
