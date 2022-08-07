package com.github.arhor.simple.expense.tracker.web.api;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.github.arhor.simple.expense.tracker.model.NotificationDTO;
import com.github.arhor.simple.expense.tracker.service.NotificationService;
import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping(path = "/subscribe", produces = TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(final Authentication auth) {
        val currentUserId = userService.determineUserId(auth);
        return notificationService.subscribe(currentUserId);
    }

    @PostMapping(path = "/unsubscribe")
    public void unsubscribe(final Authentication auth) {
        val currentUserId = userService.determineUserId(auth);
        notificationService.unsubscribe(currentUserId);
    }

    @PostMapping
    public void postNotification(
        @RequestParam final Long userId,
        @RequestBody final NotificationDTO dto,
        final Authentication auth
    ) {
        val currentUserId = userService.determineUserId(auth);
        notificationService.handleNotification(currentUserId, userId, dto);
    }
}
