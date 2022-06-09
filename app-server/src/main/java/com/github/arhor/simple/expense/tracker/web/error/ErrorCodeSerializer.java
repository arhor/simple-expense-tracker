package com.github.arhor.simple.expense.tracker.web.error;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@Slf4j
public class ErrorCodeSerializer extends StdSerializer<ErrorCode> {

    private static final byte NUM_CODE_MAX_LENGTH = 5;
    private static final char NUM_CODE_PAD_SYMBOL = '0';

    public ErrorCodeSerializer() {
        super(ErrorCode.class);
    }

    @Override
    public void serialize(final ErrorCode value, final JsonGenerator generator, final SerializerProvider provider)
        throws IOException {

        var type = value.getType().name();
        var code = convertCodeToPaddedString(value);

        generator.writeString(type + "-" + code);
    }

    private String convertCodeToPaddedString(final ErrorCode value) {
        var numberAsString = String.valueOf(value.getNumericValue());

        if (numberAsString.length() > NUM_CODE_MAX_LENGTH) {
            log.debug("ErrorCode {} numeric value is too large", value);
        }
        return StringUtils.leftPad(numberAsString, NUM_CODE_MAX_LENGTH, NUM_CODE_PAD_SYMBOL);
    }
}
