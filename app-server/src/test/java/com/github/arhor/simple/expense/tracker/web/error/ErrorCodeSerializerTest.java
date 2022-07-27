package com.github.arhor.simple.expense.tracker.web.error;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ErrorCodeSerializerTest {

    private static ErrorCodeSerializer errorCodeSerializer;

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider provider;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @BeforeAll
    static void setUpClass() {
        errorCodeSerializer = new ErrorCodeSerializer();
    }

    @AfterAll
    static void tearDownClass() {
        errorCodeSerializer = null;
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    void each_error_code_should_be_correctly_serialized_to_string(final ErrorCode errorCode) throws IOException {
        // when
        errorCodeSerializer.serialize(errorCode, generator, provider);

        // then
        then(generator)
            .should()
            .writeString(stringCaptor.capture());

        then(provider)
            .shouldHaveNoInteractions();

        assertThat(stringCaptor.getValue())
            .isNotBlank()
            .contains(
                String.valueOf(errorCode.getType()),
                String.valueOf(errorCode.getNumericValue())
            );
    }
}
