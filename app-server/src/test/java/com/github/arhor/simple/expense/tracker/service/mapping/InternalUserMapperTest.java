package com.github.arhor.simple.expense.tracker.service.mapping;

import lombok.val;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class InternalUserMapperTest extends MapperTestBase {

    @Autowired
    private InternalUserMapper userMapper;

    @Test
    void should_correctly_map_user_entity_to_response_dto() {
        // given
        val user = InternalUser.builder()
            .id(Long.MAX_VALUE)
            .username("test-username")
            .password("test-password")
            .currency("test-currency")
            .build();

        // when
        val result = userMapper.mapToResponse(user);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dto -> {
                    assertThat(dto.getId())
                        .as("id")
                        .isEqualTo(user.id());
                },
                dto -> {
                    assertThat(dto.getUsername())
                        .as("username")
                        .isEqualTo(user.username());
                },
                dto -> {
                    assertThat(dto.getCurrency())
                        .as("currency")
                        .isEqualTo(user.currency());
                });
    }

    @Test
    void should_correctly_map_user_compact_projectiob_to_response_dto() {
        // given
        val id = Long.MAX_VALUE;
        val username = "test-username";
        val currency = "test-currency";

        val user = new InternalUser.Projection(id, username, currency);

        // when
        val result = userMapper.mapToResponse(user);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dto -> {
                    assertThat(dto.getId())
                        .as("id")
                        .isEqualTo(id);
                },
                dto -> {
                    assertThat(dto.getUsername())
                        .as("username")
                        .isEqualTo(username);
                },
                dto -> {
                    assertThat(dto.getCurrency())
                        .as("currency")
                        .isEqualTo(currency);
                }
            );
    }

    @Test
    void should_correctly_map_user_request_dto_to_entity_also_encoding_password() {
        // given
        val username = "test-username";
        val password = "test-password";
        val currency = "test-currency";

        val request = new UserRequestDTO(username, password, currency);

        val encodedPassword = "encoded-test-password";

        given(passwordEncoder.encode(anyString()))
            .willReturn(encodedPassword);

        // when
        val result = userMapper.mapToUser(request);

        // then
        then(passwordEncoder)
            .should()
            .encode(request.getPassword());

        assertThat(result)
            .isNotNull()
            .satisfies(
                user -> {
                    assertThat(user.username())
                        .describedAs("username")
                        .isEqualTo(username);
                },
                user -> {
                    assertThat(user.password())
                        .describedAs("password")
                        .isEqualTo(encodedPassword);
                },
                user -> {
                    assertThat(user.currency())
                        .describedAs("currency")
                        .isEqualTo(currency);
                }
            );
    }
}
