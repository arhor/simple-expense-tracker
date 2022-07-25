package com.github.arhor.simple.expense.tracker.service.mapping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class UserMapperTest extends MapperTestBase {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void should_correctly_map_user_entity_to_response_dto() {
        // given
        var user = InternalUser.builder()
            .id(Long.MAX_VALUE)
            .username("test-username")
            .password("test-password")
            .currency("test-currency")
            .build();

        // when
        var dto = userMapper.mapToResponse(user);

        // then
        assertThat(dto)
            .isNotNull();

        assertSoftly(soft -> {
            soft.assertThat(dto.getId())
                .as("id")
                .isEqualTo(user.getId());

            soft.assertThat(dto.getUsername())
                .as("username")
                .isEqualTo(user.getUsername());

            soft.assertThat(dto.getCurrency())
                .as("currency")
                .isEqualTo(user.getCurrency());
        });
    }

    @Test
    void should_correctly_map_user_request_dto_to_entity_also_encoding_password() {
        // given
        var request = new UserRequestDTO();
        request.setUsername("test-username");
        request.setPassword("test-password");
        request.setCurrency("test-currency");

        var encodedPassword = "encoded-test-password";

        given(passwordEncoder.encode(anyString()))
            .willReturn(encodedPassword);

        // when
        var user = userMapper.mapToUser(request);

        // then
        then(passwordEncoder)
            .should()
            .encode(request.getPassword());

        assertThat(user)
            .isNotNull();

        assertSoftly(soft -> {
            soft.assertThat(user.getUsername())
                .as("username")
                .isEqualTo(request.getUsername());

            soft.assertThat(user.getPassword())
                .as("password")
                .isEqualTo(encodedPassword);

            soft.assertThat(user.getCurrency())
                .as("currency")
                .isEqualTo(request.getCurrency());
        });
    }
}
