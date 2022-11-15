package com.github.arhor.simple.expense.tracker.service.money

import javax.money.CurrencyUnit
import javax.money.convert.ConversionQuery
import javax.money.convert.ExchangeRate

val ConversionQuery.termCurrency: CurrencyUnit?
    get() = currency

val ExchangeRate.termCurrency: CurrencyUnit?
    get() = currency
