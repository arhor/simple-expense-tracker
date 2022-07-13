package com.github.arhor.simple.expense.tracker.web.api;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.TimeService;
import com.github.arhor.simple.expense.tracker.service.UserService;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest extends BaseControllerTest {

    @MockBean
    private ExpenseService expenseService;
    @MockBean
    private TimeService timeService;
    @MockBean
    private UserService userService;
}
