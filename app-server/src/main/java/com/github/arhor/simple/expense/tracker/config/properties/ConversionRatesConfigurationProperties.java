package com.github.arhor.simple.expense.tracker.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @param dataFilePattern path-pattern for the data-files containing historical conversion-rates
 * @param yearsToLoad     number of previous years to load starting from the last year
 */
@ConstructorBinding
@ConfigurationProperties("configuration.conversion-rates")
public record ConversionRatesConfigurationProperties(
    String dataFilePattern,
    int yearsToLoad
) {
}
