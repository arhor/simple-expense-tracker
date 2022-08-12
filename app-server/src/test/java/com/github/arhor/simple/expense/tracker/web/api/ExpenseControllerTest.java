package com.github.arhor.simple.expense.tracker.web.api;

import lombok.val;

import java.time.LocalDate;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;
import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria;
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.TimeService;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest extends BaseControllerTest {

    @Autowired
    private ApplicationProps applicationProps;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseItemService expenseItemService;

    @MockBean
    private TimeService timeService;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<Authentication> authCaptor;

    @Test
    @WithMockUser
    void should_return_status_200_and_empty_expense_list() throws Exception {
        // given
        val expensesEndPoint = applicationProps.apiUrlPath("/expenses");

        val expectedCriteria = new DateRangeCriteria(null, null);
        val currentTimeZone = TimeZone.getDefault();

        // when
        val result = http.perform(get(expensesEndPoint));

        // then
        then(userService)
            .should()
            .determineUserId(authCaptor.capture());
        then(timeService)
            .should()
            .convertToDateRange(expectedCriteria, currentTimeZone);

        assertThat(authCaptor.getValue())
            .isNotNull()
            .satisfies(this::authenticatedUser);

        result
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    void should_return_status_200_and_expense_list_with_expected_content() throws Exception {
        // given
        val expensesEndPoint = applicationProps.apiUrlPath("/expenses");

        val today = LocalDate.now();
        val expectedCriteria = new DateRangeCriteria(today.minusWeeks(1), today);
        val currentTimeZone = TimeZone.getDefault();

        // when
        val result = http.perform(
            get(expensesEndPoint)
                .queryParam("startDate", String.valueOf(expectedCriteria.startDate()))
                .queryParam("endDate", String.valueOf(expectedCriteria.endDate()))
        );

        // then
        then(userService)
            .should()
            .determineUserId(authCaptor.capture());
        then(timeService)
            .should()
            .convertToDateRange(expectedCriteria, currentTimeZone);

        assertThat(authCaptor.getValue())
            .isNotNull()
            .satisfies(this::authenticatedUser);

        result
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
}
