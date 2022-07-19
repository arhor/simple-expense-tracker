package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

public interface TotalCalculator {

    void add(final MonetaryAmount amount, final CurrencyUnit currency, final LocalDate date);

    MonetaryAmount getTotal();
}
