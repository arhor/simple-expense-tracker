package com.github.arhor.simple.expense.tracker.web.api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;

import com.github.arhor.simple.expense.tracker.config.LocalizationConfig;
import com.github.arhor.simple.expense.tracker.model.UserResponse;
import com.github.arhor.simple.expense.tracker.service.UserService;
import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

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
