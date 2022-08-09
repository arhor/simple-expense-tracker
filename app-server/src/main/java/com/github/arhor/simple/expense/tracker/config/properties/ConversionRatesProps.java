package com.github.arhor.simple.expense.tracker.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @param pattern path-pattern for the data-files containing historical conversion-rates
 * @param preload number of previous years to preload starting from the last year
 */
@ConstructorBinding
@ConfigurationProperties("configuration.conversion-rates")
public record ConversionRatesProps(String pattern, int preload) {
}
