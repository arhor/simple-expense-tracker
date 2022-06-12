package com.github.arhor.simple.expense.tracker.config;

import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MoneyOperationsConfig {

    @Bean
    public ExchangeRateProvider exchangeRateProvider() {
        return MonetaryConversions.getExchangeRateProvider();
    }
}
