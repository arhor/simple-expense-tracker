package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

public interface MoneyConverter {

    <T extends MonetaryAmount> T convert(T amount, CurrencyUnit targetCurrency, LocalDate date);
}
