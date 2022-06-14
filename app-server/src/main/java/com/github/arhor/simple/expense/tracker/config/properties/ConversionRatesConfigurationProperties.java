package com.github.arhor.simple.expense.tracker.config.properties;

import lombok.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
@ConfigurationProperties("configuration.conversion-rates")
public class ConversionRatesConfigurationProperties {
    /**
     * Path-pattern for the data-files containing historical conversion-rates.
     */
    String dataFilePattern;

    /**
     * Number of previous years to load starting from the last year.
     */
    int yearsToLoad;
}
