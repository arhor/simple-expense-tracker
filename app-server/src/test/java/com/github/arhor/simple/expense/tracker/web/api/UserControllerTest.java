package com.github.arhor.simple.expense.tracker.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.arhor.simple.expense.tracker.model.UserResponse;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

        var requestBody = """
            {
                "username": "%s",
                "password": "%s",
                "currency": "%s"
            }
            """.formatted(username, password, currency);

        var response = new UserResponse();
        response.setId(1L);
        response.setUsername(username);
        response.setCurrency(currency);

        given(userService.createNewUser(any()))
            .willReturn(response);

        // when
        http.perform(post("/api/users").content(requestBody).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", equalTo(response.getId()), long.class))
            .andExpect(jsonPath("$.username", equalTo(response.getUsername())))
            .andExpect(jsonPath("$.currency", equalTo(response.getCurrency())));

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
        http.perform(get("/api/users?current"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(response.getId()), long.class))
            .andExpect(jsonPath("$.username", equalTo(response.getUsername())))
            .andExpect(jsonPath("$.currency", equalTo(response.getCurrency())));

        // then
        then(userService)
            .should()
            .determineUser(argThat(it -> it.isAuthenticated() && "user".equals(it.getName())));
    }
}
