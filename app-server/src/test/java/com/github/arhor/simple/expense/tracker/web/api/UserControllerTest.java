package com.github.arhor.simple.expense.tracker.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.arhor.simple.expense.tracker.CustomArgumentMatchers;
import com.github.arhor.simple.expense.tracker.model.UserResponse;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    @Test
    void should_return_status_201_user_info_and_location_header_creating_new_user() throws Exception {
        // given
        var username = "username";
        var password = "Password1";
        var currency = "USD";

        var response = new UserResponse();
        response.setId(1L);
        response.setUsername(username);
        response.setCurrency(currency);

        given(userService.createNewUser(any()))
            .willReturn(response);

        // when
        var result = http.perform(
            post("/api/users")
                .contentType("application/json")
                .content("""
                    {
                        "username": "%s",
                        "password": "%s",
                        "currency": "%s"
                    }
                    """.formatted(username, password, currency))
        );

        // then
        then(userService)
            .should()
            .createNewUser(
                argThat(request -> {
                    return username.equals(request.getUsername())
                        && password.equals(request.getPassword())
                        && currency.equals(request.getCurrency());
                })
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
        var response = new UserResponse();
        response.setId(1L);
        response.setUsername("user");
        response.setCurrency("USD");

        given(userService.determineUser(any()))
            .willReturn(response);

        // when
        var result = http.perform(get("/api/users?current"));

        // then
        then(userService)
            .should()
            .determineUser(argThat(CustomArgumentMatchers::authenticatedUser));

        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.username").value(response.getUsername()))
            .andExpect(jsonPath("$.currency").value(response.getCurrency()));
    }
}
