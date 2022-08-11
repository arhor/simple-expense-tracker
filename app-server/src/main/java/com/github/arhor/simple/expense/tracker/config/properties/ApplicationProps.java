package com.github.arhor.simple.expense.tracker.config.properties;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("application-props")
public record ApplicationProps(
    String apiPathPrefix,
    Optional<Resources> resources,
    ConversionRates conversionRates
) {

    @ConstructorBinding
    ApplicationProps(
        final @DefaultValue("/api") String apiPathPrefix,
        final Resources resources,
        final ConversionRates conversionRates
    ) {
        this(apiPathPrefix, Optional.ofNullable(resources), conversionRates);
    }

    public String apiUrlPath(final String url) {
        return apiPathPrefix + url;
    }

    /**
     * @param patterns path patterns for the static resources
     * @param location locations to lookup for the static resources
     */
    public record Resources(
        List<String> patterns,
        List<String> location
    ) {}

    /**
     * @param pattern path-pattern for the data-files containing historical conversion-rates
     * @param preload number of previous years to preload starting from the last year
     */
    public record ConversionRates(
        String pattern,
        int preload
    ) {}
}