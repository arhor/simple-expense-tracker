package com.github.arhor.simple.expense.tracker.web.error;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ErrorCodeSerializer extends StdSerializer<ErrorCode> {

    public static final byte CODE_MAX_LENGTH = 5;
    public static final char CODE_PAD_SYMBOL = '0';

    public ErrorCodeSerializer() {
        super(ErrorCode.class);
    }

    @Override
    public void serialize(final ErrorCode value, final JsonGenerator generator, final SerializerProvider provider)
        throws IOException {

        var type = value.getType();
        var code = StringUtils.leftPad(Integer.toHexString(value.getNumericValue()), CODE_MAX_LENGTH, CODE_PAD_SYMBOL);

        var result = (type + "-" + code).toUpperCase();

        generator.writeString(result);
    }
}
