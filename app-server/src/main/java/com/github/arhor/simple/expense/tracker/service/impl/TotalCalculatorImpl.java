package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.service.MoneyConverter;
import com.github.arhor.simple.expense.tracker.service.TotalCalculator;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@Component
@Scope(SCOPE_PROTOTYPE)
public class TotalCalculatorImpl implements TotalCalculator {

    @Autowired
    private MoneyConverter converter;
    private Money total;

    public TotalCalculatorImpl(final CurrencyUnit currency) {
        total = Money.zero(currency);
    }

    @Override
    public void add(final MonetaryAmount amount, final CurrencyUnit currency, final LocalDate date) {
        total = total.add(converter.convert(amount, currency, date));
    }

    @Override
    public MonetaryAmount getTotal() {
        return total;
    }
}
