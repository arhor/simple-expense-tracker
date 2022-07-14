package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.ExchangeRateProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.service.MoneyConverter;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MoneyConverterImpl implements MoneyConverter {

    private final ExchangeRateProvider exchangeRateProvider;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MonetaryAmount> T convert(
        final T amount,
        final CurrencyUnit targetCurrency,
        final LocalDate date
    ) {
        var sourceCurrency = amount.getCurrency();
        var conversionDate = determineConversionDate(date);
        var providerName = determineProviderName(conversionDate);

        var query = ConversionQueryBuilder.of()
            .setBaseCurrency(sourceCurrency)
            .setTermCurrency(targetCurrency)
            .setProviderName(providerName)
            .set(conversionDate)
            .build();

        var currencyConversion = exchangeRateProvider.getCurrencyConversion(query);

        return (T) amount.with(currencyConversion);
    }

    /**
     * ECB does not provide exchange rates on Saturday, Sunday, and public holidays.
     *
     * @see <a href="https://www.ecb.europa.eu/services/contacts/working-hours/html/index.en.html">ECB working hours</a>
     */
    private LocalDate determineConversionDate(final LocalDate date) {

        // TODO: consider also public holidays according to the ECB
        return switch (date.getDayOfWeek()) {
            case SATURDAY -> date.minusDays(1);
            case SUNDAY -> date.minusDays(2);
            default -> date;
        };
    }

    /**
     * Since {@link ExchangeRateProvider#isAvailable} default implementation checks only provider name in the query,
     * returning {@literal true} in case no provider names specified, it should be set manually.
     *
     * <p>
     * <strong>Reason</strong>: Consider {@link org.javamoney.moneta.spi.CompoundRateProvider} class - its
     * {@link ExchangeRateProvider#getExchangeRate} method iterates through the list of nested providers, trying to
     * determine which one to use via {@link ExchangeRateProvider#isAvailable} method. Without provider name specified,
     * it will use first provider in the list, even though it may not provide exchange rates for the date set in the
     * query.
     * </p>
     */
    private String determineProviderName(final LocalDate conversionDate) {
        var currentDate = LocalDate.now();
        var period = conversionDate.until(currentDate);
        var daysPassedFromConversion = period.getDays();

        String provider;
        if (daysPassedFromConversion <= 0) {
            provider = "EXCHANGERATE_HOST";
        } else if (daysPassedFromConversion <= 90) {
            provider = "EXCHANGERATE_HOST";
        } else {
            provider = "EXCHANGERATE_HOST";
        }
        return provider;
    }
}
