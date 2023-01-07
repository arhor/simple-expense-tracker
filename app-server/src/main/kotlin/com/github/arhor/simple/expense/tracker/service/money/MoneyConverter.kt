package com.github.arhor.simple.expense.tracker.service.money

import java.time.LocalDate
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount

interface MoneyConverter {

    /**
     * Converts given monetary amount to the target currency using exchange rates on the given date.
     *
     * @param amount         amount to convert
     * @param currency       target currency
     * @param conversionDate conversion date
     * @param T              type of the concrete monetary amount
     *
     * @return converted monetary amount
     */
    fun <T> convert(amount: T, currency: CurrencyUnit, conversionDate: LocalDate): MonetaryAmount
        where T : MonetaryAmount
}
