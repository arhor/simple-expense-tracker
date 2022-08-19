package com.github.arhor.simple.expense.tracker.web.controller;

import lombok.val;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private ApplicationProps applicationProps;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<Authentication> authCaptor;

    @Captor
    private ArgumentCaptor<UserRequestDTO> userCaptor;

    @Test
    void should_return_status_201_user_info_and_location_header_creating_new_user() throws Exception {
        // given
        val usersEndPoint = applicationProps.apiUrlPath("/users");

        val username = "username";
        val password = "Password1";
        val currency = "USD";

        // language=JSON
        val requestBody = """
            {
                "username": "%s",
                "password": "%s",
                "currency": "%s"
            }
            """.formatted(username, password, currency);

        val response = new UserResponseDTO();
        response.setId(1L);
        response.setUsername(username);
        response.setCurrency(currency);

        given(userService.createNewUser(any()))
            .willReturn(response);

        // when
        val result = http.perform(
            post(usersEndPoint)
                .contentType("application/json")
                .content(requestBody)
        );

        // then
        then(userService)
            .should()
            .createNewUser(userCaptor.capture());

        assertThat(userCaptor.getValue())
            .satisfies(
                dto -> {
                    assertThat(dto.getUsername())
                        .as("username")
                        .isNotNull()
                        .isEqualTo(username);
                },
                dto -> {
                    assertThat(dto.getPassword())
                        .as("password")
                        .isNotNull()
                        .isEqualTo(password);
                },
                dto -> {
                    assertThat(dto.getCurrency())
                        .as("currency")
                        .isNotNull()
                        .isEqualTo(currency);
                }
            );

        result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.username").value(response.getUsername()))
            .andExpect(jsonPath("$.currency").value(response.getCurrency()));
    }

    @Test
    @WithMockUser
    void should_return_status_200_and_expected_info_for_authenticated_user() throws Exception {
        // given
        val usersEndPoint = applicationProps.apiUrlPath("/users");

        val response = new UserResponseDTO();
        response.setId(1L);
        response.setUsername("user");
        response.setCurrency("USD");

        given(userService.determineUser(any()))
            .willReturn(response);

        // when
        val result = http.perform(
            get(usersEndPoint)
                .queryParam("current", "true")
        );

        // then
        then(userService)
            .should()
            .determineUser(authCaptor.capture());

        assertThat(authCaptor.getValue())
            .isNotNull()
            .satisfies(this::authenticatedUser);

        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.username").value(response.getUsername()))
            .andExpect(jsonPath("$.currency").value(response.getCurrency()));
    }
}
