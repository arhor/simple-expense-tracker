package com.github.arhor.simple.expense.tracker.service.mapping;

import lombok.RequiredArgsConstructor;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PasswordEncoderConverter {

    private final PasswordEncoder encoder;

    @Named("encode")
    public String encode(final String value) {
        return encoder.encode(value);
    }
}
