package com.github.arhor.simple.expense.tracker.service.mapping;

import java.util.*

object OptionalMapper {

    @JvmStatic
    fun <T : Any> wrap(value: T): Optional<T> = Optional.ofNullable(value)

    @JvmStatic
    fun <T> unwrap(value: Optional<T>): T? = value.orElse(null)
}
