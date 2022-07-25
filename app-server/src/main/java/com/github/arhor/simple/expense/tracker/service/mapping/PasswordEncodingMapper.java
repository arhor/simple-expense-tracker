package com.github.arhor.simple.expense.tracker.service.mapping;

import lombok.RequiredArgsConstructor;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PasswordEncodingMapper {

    private final PasswordEncoder encoder;

    @Named("encodePassword")
    public String encodePassword(final String value) {
        return encoder.encode(value);
    }
}
