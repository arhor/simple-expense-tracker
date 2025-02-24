package com.github.arhor.simple.expense.tracker.api.error

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * Serializes [ErrorCode] instance to the string prefixed with corresponding [ErrorCode.type],
 * also including error code numeric value with fixed length.
 */
class ErrorCodeSerializer : StdSerializer<ErrorCode>(ErrorCode::class.java) {

    override fun serialize(value: ErrorCode, generator: JsonGenerator, provider: SerializerProvider) {
        val errorCodeHexString = Integer.toHexString(value.value)

        val type = value.type
        val code = errorCodeHexString.padStart(CODE_MAX_LENGTH, CODE_PAD_SYMBOL)

        val result = "$type-$code".uppercase()

        generator.writeString(result)
    }

    companion object {
        const val CODE_MAX_LENGTH = 5
        const val CODE_PAD_SYMBOL = '0'
    }
}
