package com.github.arhor.simple.expense.tracker.web.api;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.arhor.simple.expense.tracker.service.NotificationService;
import com.github.arhor.simple.expense.tracker.service.UserService;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest extends BaseControllerTest {

    @MockBean
    private NotificationService notificationService;
    @MockBean
    private UserService userService;

}
