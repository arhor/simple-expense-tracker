package com.github.arhor.simple.expense.tracker.web.api;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.TimeService;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest extends BaseControllerTest {

    @MockBean
    private ExpenseService expenseService;
    @MockBean
    private TimeService timeService;
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void should_return_status_200_and_empty_expense_list() throws Exception {
        // given
        var currentTimeZone = TimeZone.getDefault();

        // when
        http.perform(get("/api/expenses"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

        // then
        then(userService)
            .should()
            .determineUserId(argThat(it -> it.isAuthenticated() && "user".equals(it.getName())));
        then(timeService)
            .should()
            .convertToDateRange(null, null, currentTimeZone);
    }

    @Test
    @WithMockUser
    void should_return_status_200_and_expense_list_with_expected_content() throws Exception {
        // given
        var startDate = LocalDate.now().minusDays(7);
        var endDate = LocalDate.now();
        var currentTimeZone = TimeZone.getDefault();

        // when
        http.perform(get("/api/expenses")
                .queryParam("startDate", String.valueOf(startDate))
                .queryParam("endDate", String.valueOf(endDate)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

        // then
        then(userService)
            .should()
            .determineUserId(argThat(it -> it.isAuthenticated() && "user".equals(it.getName())));
        then(timeService)
            .should()
            .convertToDateRange(startDate, endDate, currentTimeZone);
    }
}
