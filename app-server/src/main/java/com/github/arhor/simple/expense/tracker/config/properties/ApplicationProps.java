package com.github.arhor.simple.expense.tracker.config.properties;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("application-props")
public record ApplicationProps(
    Optional<String> apiPathPrefix,
    Optional<Resources> resources,
    Optional<ConversionRates> conversionRates
) {

    @ConstructorBinding
    ApplicationProps(
        final String apiPathPrefix,
        final Resources resources,
        final ConversionRates conversionRates
    ) {
        this(
            Optional.ofNullable(apiPathPrefix),
            Optional.ofNullable(resources),
            Optional.ofNullable(conversionRates)
        );
    }

    public String apiUrlPath(final String url) {
        return apiPathPrefix.orElse("") + url;
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
