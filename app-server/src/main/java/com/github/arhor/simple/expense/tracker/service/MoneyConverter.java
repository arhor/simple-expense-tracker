package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

public interface MoneyConverter {

    MonetaryAmount convert(MonetaryAmount amount, CurrencyUnit targetCurrency, LocalDate date);
}
